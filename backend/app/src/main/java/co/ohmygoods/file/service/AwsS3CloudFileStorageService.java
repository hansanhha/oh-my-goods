package co.ohmygoods.file.service;

import co.ohmygoods.file.config.AwsS3CloudFileStorageProperties;
import co.ohmygoods.file.service.dto.UploadFileRequest;
import co.ohmygoods.file.service.dto.UploadFileResponse;
import co.ohmygoods.file.service.vo.CloudStorageProvider;
import co.ohmygoods.file.service.vo.StorageStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AwsS3CloudFileStorageService implements FileStorageService {

    private final AwsS3CloudFileStorageProperties properties;

    @Override
    public List<UploadFileResponse> upload(UploadFileRequest request) {
        return Collections.emptyList();
    }

    @Override
    public Optional<InputStream> download(String path) {
        return null;
    }

    @Override
    public void delete(String path) {

    }

    @Override
    public Optional<URI> getCloudStorageAccessURL() {
        return FileStorageService.super.getCloudStorageAccessURL();
    }

    @Override
    public boolean canSupport(StorageStrategy storageStrategy, CloudStorageProvider cloudStorageProvider) {
        return cloudStorageProvider.equals(CloudStorageProvider.AWS_S3)
                &&
                (storageStrategy.equals(StorageStrategy.CLOUD_STORAGE_API)
                        || storageStrategy.equals(StorageStrategy.PROVIDE_CLOUD_STORAGE_ACCESS_URL));
    }
}
