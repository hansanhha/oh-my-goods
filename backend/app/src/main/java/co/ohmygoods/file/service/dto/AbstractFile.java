package co.ohmygoods.file.service.dto;

import co.ohmygoods.file.service.vo.FileType;

import java.io.InputStream;
import java.util.Optional;

public abstract class AbstractFile implements File {

    @Override
    public Optional<InputStream> getInputStream() {
        if (isDefaultStorageStrategy()) {
            StorageProvider storageProvider = getStorageProvider();

            return switch (storageProvider) {
                case AWS_S3 -> getInputStreamFromAwsS3();
                case LOCAL_FILE_SYSTEM -> getInputStreamFromLocal();
            };
        }

        return Optional.empty();
    }

    @Override
    public Optional<byte[]> getBytes() {
        if (isDefaultStorageStrategy()) {
            StorageProvider storageProvider = getStorageProvider();

            return switch (storageProvider) {
                case AWS_S3 -> getBytesFromAwsS3();
                case LOCAL_FILE_SYSTEM -> getBytesFromLocal();
            };
        }

        return Optional.empty();
    }

    @Override
    public boolean isSupportedFileType(FileType fileType) {
        return getFileType().equals(fileType);
    }

    private boolean isDefaultStorageStrategy() {
        return getStorageStrategy().equals(StorageStrategy.DEFAULT);
    }

    private Optional<InputStream> getInputStreamFromLocal() {

    }

    private Optional<InputStream> getInputStreamFromAwsS3() {

    }

    private Optional<byte[]> getBytesFromLocal() {

    }

    private Optional<byte[]> getBytesFromAwsS3() {

    }
}
