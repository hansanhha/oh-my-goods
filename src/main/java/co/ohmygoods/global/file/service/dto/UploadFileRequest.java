package co.ohmygoods.global.file.service.dto;

import co.ohmygoods.global.file.model.vo.CloudStorageProvider;
import co.ohmygoods.global.file.model.vo.DomainType;
import co.ohmygoods.global.file.model.vo.StorageStrategy;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public record UploadFileRequest(StorageStrategy storageStrategy,
                                CloudStorageProvider cloudStorageProvider,
                                String uploaderEmail,
                                DomainType targetDomain,
                                @Nullable Collection<String> domainIds,
                                @Nullable Map<String, MultipartFile> domainIdFileMap) {

    public static UploadFileRequest useLocalFileSystem(String uploaderEmail,
                                                       DomainType domainType,
                                                       Map<String, MultipartFile> domainIdFileMap) {
        return new UploadFileRequest(StorageStrategy.LOCAL_FILE_SYSTEM, null,
                uploaderEmail, domainType, null, domainIdFileMap);
    }

    public static UploadFileRequest useCloudStorage(CloudStorageProvider cloudStorageProvider,
                                                    String uploaderEmail,
                                                    DomainType domainType,
                                                    Map<String, MultipartFile> domainIdFileMap) {
        return new UploadFileRequest(StorageStrategy.CLOUD_STORAGE_API, cloudStorageProvider,
                uploaderEmail, domainType, null, domainIdFileMap);
    }

    public static UploadFileRequest useCloudStorageAccessURL(CloudStorageProvider cloudStorageProvider,
                                                             String uploaderEmail,
                                                             DomainType domainType,
                                                             Collection<String> domainIds) {
        return new UploadFileRequest(StorageStrategy.PROVIDE_CLOUD_STORAGE_ACCESS_URL, cloudStorageProvider,
                uploaderEmail, domainType, domainIds, null);
    }

    public static UploadFileRequest from(StorageStrategy storageStrategy,
                                         CloudStorageProvider cloudStorageProvider,
                                         String uploaderEmail,
                                         DomainType domainType,
                                         Collection<String> domainIds,
                                         HashMap<String, MultipartFile> imageInfoIdFileMap) {

        if (storageStrategy == null) {
            return useCloudStorage(cloudStorageProvider != null ? cloudStorageProvider : CloudStorageProvider.DEFAULT, uploaderEmail, domainType, imageInfoIdFileMap);
        }

        return switch (storageStrategy) {
            case LOCAL_FILE_SYSTEM -> useLocalFileSystem(uploaderEmail, domainType, imageInfoIdFileMap);
            case CLOUD_STORAGE_API -> useCloudStorage(cloudStorageProvider,uploaderEmail,domainType,imageInfoIdFileMap);
            case PROVIDE_CLOUD_STORAGE_ACCESS_URL -> useCloudStorageAccessURL(cloudStorageProvider, uploaderEmail, domainType, domainIds);
        };
    }
}
