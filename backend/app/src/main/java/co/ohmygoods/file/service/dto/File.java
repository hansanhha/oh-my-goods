package co.ohmygoods.file.service.dto;

import co.ohmygoods.file.service.vo.FileType;

import java.io.InputStream;
import java.util.Optional;

public interface File {

    StorageProvider getStorageProvider();
    StorageStrategy getStorageStrategy();

    String getStoragePath();
    String getFileName();
    String getOriginalFileName();

    String getContentType();
    FileType getFileType();
    long getSize();

    Optional<InputStream> getInputStream();
    Optional<byte[]> getBytes();

    boolean isSupportedFileType(FileType fileType);

    enum StorageProvider {
        AWS_S3,
        LOCAL_FILE_SYSTEM;
    }

    enum StorageStrategy {
        /*
            허용된 클라우드 스토리지 URL 주소를 프론트엔드에게 제공
            프론트엔드에서 직접 파일 업로드
         */
        PROVIDE_ALLOWED_CLOUD_STORAGE_URL,
        DEFAULT;
    }
}
