package co.ohmygoods.file.service;

import co.ohmygoods.file.config.LocalFileStorageProperties;
import co.ohmygoods.file.exception.FileException;
import co.ohmygoods.file.service.dto.UploadFileRequest;
import co.ohmygoods.file.service.dto.UploadFileResponse;
import co.ohmygoods.file.model.vo.CloudStorageProvider;
import co.ohmygoods.file.model.vo.StorageStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SimpleLocalFileStorageService implements FileStorageService {

    private final LocalFileStorageProperties properties;

    @Override
    public List<UploadFileResponse> upload(UploadFileRequest request) {
        Path directoryPath = createDirectoryPath(request.uploaderEmail());

        return request
                .files()
                .stream()
                .map(file -> {
                    String fileName = resolveFileName(file);
                    Path filePath = directoryPath.resolve(fileName);

                    try (FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile())) {
                        fileOutputStream.write(file.getBytes());
                    } catch (IOException e) {
                        throw new FileException();
                    }

                    return new UploadFileResponse(fileName, file.getContentType(), filePath.toString());
                })
                .toList();
    }

    @Override
    public Optional<InputStream> download(String path) {
        File file = new File(path);

        if (isInvalidFile(file)) {
            return Optional.empty();
        }

        try {
            return Optional.of(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }

    }

    @Override
    public void delete(String path) {
    }

    @Override
    public boolean canSupport(StorageStrategy storageStrategy, CloudStorageProvider cloudStorageProvider) {
        return storageStrategy.equals(StorageStrategy.LOCAL_FILE_SYSTEM);
    }

    private boolean isInvalidFile(File file) {
        return !file.exists() || !file.isFile() || !file.canRead();
    }

    private Path createDirectoryPath(String uploaderEmail) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String targetDirectory = properties.getBaseDirectory() + File.separator + currentDate + File.separator + uploaderEmail;

        Path directoryPath = Paths.get(targetDirectory);

        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
        } catch (
                IOException e) {
            throw new FileException();
        }

        return directoryPath;
    }

    private String resolveFileName(MultipartFile file) {
        StringBuilder fileName = new StringBuilder(String.valueOf(System.currentTimeMillis()));
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        if (StringUtils.hasText(extension)) {
            fileName.append(".").append(extension);
        }

        return fileName.toString();
    }
}
