package co.ohmygoods.file.service.dto;

import co.ohmygoods.file.model.vo.CloudStorageProvider;
import co.ohmygoods.file.model.vo.DomainType;
import co.ohmygoods.file.model.vo.StorageStrategy;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public record UploadFileRequest(StorageStrategy storageStrategy,
                                CloudStorageProvider cloudStorageProvider,
                                String uploaderEmail,
                                DomainType targetDomain,
                                String domainId,
                                Collection<? extends MultipartFile> files) {

    public static UploadFileRequest localFileSystem(String uploaderEmail,
                                                    DomainType domainType,
                                                    String domainId,
                                                    Collection<? extends MultipartFile> files) {
        return new UploadFileRequest(StorageStrategy.LOCAL_FILE_SYSTEM, null,
                uploaderEmail, domainType, domainId, files);
    }

    public static UploadFileRequest cloudFileStorage(CloudStorageProvider cloudStorageProvider,
                                                     String uploaderEmail,
                                                     DomainType domainType,
                                                     String domainId,
                                                     Collection<? extends MultipartFile> files) {
        return new UploadFileRequest(StorageStrategy.CLOUD_STORAGE_API, cloudStorageProvider,
                uploaderEmail, domainType, domainId, files);
    }

    public static UploadFileRequest cloudFileStorageAccessURL(CloudStorageProvider cloudStorageProvider,
                                                              String uploaderEmail,
                                                              DomainType domainType,
                                                              String domainId) {
        return new UploadFileRequest(StorageStrategy.PROVIDE_CLOUD_STORAGE_ACCESS_URL, cloudStorageProvider,
                uploaderEmail, domainType, domainId, null);
    }
}
