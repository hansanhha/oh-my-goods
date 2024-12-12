package co.ohmygoods.file.service;

import co.ohmygoods.file.config.AwsS3CloudStorageProperties;
import co.ohmygoods.file.exception.FileException;
import co.ohmygoods.file.model.vo.CloudStorageProvider;
import co.ohmygoods.file.model.vo.StorageStrategy;
import co.ohmygoods.file.service.dto.UploadFileRequest;
import co.ohmygoods.file.service.dto.UploadFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
                        throw new FileException();
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
}
