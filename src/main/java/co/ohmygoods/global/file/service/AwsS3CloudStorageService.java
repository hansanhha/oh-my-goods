package co.ohmygoods.global.file.service;

import co.ohmygoods.global.file.config.AwsS3CloudStorageProperties;
import co.ohmygoods.global.file.exception.FileException;
import co.ohmygoods.global.file.model.vo.CloudStorageProvider;
import co.ohmygoods.global.file.model.vo.StorageStrategy;
import co.ohmygoods.global.file.service.dto.UploadFileRequest;
import co.ohmygoods.global.file.service.dto.UploadFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AwsS3CloudStorageService extends AbstractStorageService {

    private final S3Client awsS3Client;
    private final AwsS3CloudStorageProperties properties;

    @Override
    public List<UploadFileResponse> upload(UploadFileRequest request) {
        if (request.domainIdFileMap() == null || request.domainIdFileMap().isEmpty()) {
            throw FileException.EMPTY_UPLOAD_FILE;
        }

        createBucketIfRequire(properties.getBucketName());

        return request.domainIdFileMap()
                .entrySet()
                .stream()
                .map(entry -> {
                    String domainId = entry.getKey();
                    MultipartFile file = entry.getValue();

                    String directoryPath = createDirectoryPath(getBaseDirectoryNameByFileType(file), request.uploaderEmail());
                    String fileName = resolveFileName(file);
                    String filePath = directoryPath.concat(fileName);
                    String fileContentType = file.getContentType();

                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(properties.getBucketName())
                            .key(filePath)
                            .contentType(fileContentType)
                            .contentLength(file.getSize())
                            .build();

                    try {
                        awsS3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
                    } catch (IOException e) {
                        throw FileException.FAILED_FILE_UPLOAD;
                    }

                    return  new UploadFileResponse(domainId, fileName, fileContentType, filePath);
                })
                .toList();
    }

    @Override
    public URL getFileAccessUrl(String path) {
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(properties.getBucketName()).key(path).build();

        return awsS3Client.utilities().getUrl(request);
    }

    @Override
    public void delete(String path) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(properties.getBucketName()).key(path).build();

        awsS3Client.deleteObject(request);
    }

    @Override
    public Optional<URI> getCloudStorageAccessURL() {
        return Optional.empty();
    }

    @Override
    public boolean canSupport(StorageStrategy storageStrategy, CloudStorageProvider cloudStorageProvider) {
        return cloudStorageProvider.equals(CloudStorageProvider.AWS_S3)
                &&
                (storageStrategy.equals(StorageStrategy.CLOUD_STORAGE_API)
                        || storageStrategy.equals(StorageStrategy.PROVIDE_CLOUD_STORAGE_ACCESS_URL));
    }

    private void createBucketIfRequire(String bucketName) {

        List<Bucket> buckets = awsS3Client.listBuckets().buckets();

        if (buckets.isEmpty() || buckets.stream().noneMatch(bucket -> bucket.name().equals(bucketName))) {
            return;
        }

        awsS3Client.createBucket(
                CreateBucketRequest.builder().bucket(bucketName).build());
    }
}
