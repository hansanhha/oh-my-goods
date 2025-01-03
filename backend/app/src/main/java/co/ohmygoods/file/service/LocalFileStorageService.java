package co.ohmygoods.file.service;

import co.ohmygoods.file.config.LocalFileStorageProperties;
import co.ohmygoods.file.exception.FileException;
import co.ohmygoods.file.model.vo.CloudStorageProvider;
import co.ohmygoods.file.model.vo.StorageStrategy;
import co.ohmygoods.file.service.dto.UploadFileRequest;
import co.ohmygoods.file.service.dto.UploadFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LocalFileStorageService extends AbstractStorageService {

    private final LocalFileStorageProperties properties;

    @Override
    public List<UploadFileResponse> upload(UploadFileRequest request) {
        Path directoryPath = createDirectoryPath(request.uploaderEmail());

        if (request.domainIdFileMap() == null || request.domainIdFileMap().isEmpty()) {
            return Collections.emptyList();
        }

        return request
                .domainIdFileMap()
                .entrySet()
                .stream()
                .map((entry) -> {
                    String domainId = entry.getKey();
                    MultipartFile file = entry.getValue();

                    String fileName = resolveFileName(file);
                    Path filePath = directoryPath.resolve(fileName);

                    try (FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile())) {
                        fileOutputStream.write(file.getBytes());
                    } catch (IOException e) {
                        throw FileException.FAILED_FILE_UPLOAD;
                    }

                    return new UploadFileResponse(domainId, fileName, file.getContentType(), filePath.toString());
                })
                .toList();
    }

    @Override
    public InputStream download(String path) {
        File file = new File(path);

        if (isInvalidFile(file)) {
            throw FileException.INVALID_FILE;
        }

        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw FileException.FAILED_FILE_DOWNLOAD;
        }

    }

    @Override
    public void delete(String path) {
        File file = new File(path);

        if (!isInvalidFile(file)) {
            file.delete();
        }
    }

    @Override
    public boolean canSupport(StorageStrategy storageStrategy, CloudStorageProvider cloudStorageProvider) {
        return storageStrategy.equals(StorageStrategy.LOCAL_FILE_SYSTEM);
    }

    private boolean isInvalidFile(File file) {
        return !file.exists() || !file.isFile() || !file.canRead();
    }

    private Path createDirectoryPath(String uploaderEmail) {
        String targetDirectory = super.createDirectoryPath(properties.getBaseDirectory(), uploaderEmail);

        Path directoryPath = Paths.get(targetDirectory);

        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
        } catch (
                IOException e) {
            throw FileException.FAILED_CREATE_DIRECTORY;
        }

        return directoryPath;
    }

}
