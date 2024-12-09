package co.ohmygoods.file.service.dto;

import co.ohmygoods.file.model.vo.CloudStorageProvider;
import co.ohmygoods.file.model.vo.DomainType;
import co.ohmygoods.file.model.vo.StorageStrategy;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.util.Collection;
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

    public static UploadFileRequest useCloudFileStorage(CloudStorageProvider cloudStorageProvider,
                                                        String uploaderEmail,
                                                        DomainType domainType,
                                                        Map<String, MultipartFile> domainIdFileMap) {
        return new UploadFileRequest(StorageStrategy.CLOUD_STORAGE_API, cloudStorageProvider,
                uploaderEmail, domainType, null, domainIdFileMap);
    }

    public static UploadFileRequest useCloudFileStorageAccessURL(CloudStorageProvider cloudStorageProvider,
                                                                 String uploaderEmail,
                                                                 DomainType domainType,
                                                                 Collection<String> domainIds) {
        return new UploadFileRequest(StorageStrategy.PROVIDE_CLOUD_STORAGE_ACCESS_URL, cloudStorageProvider,
                uploaderEmail, domainType, domainIds, null);
    }
}
