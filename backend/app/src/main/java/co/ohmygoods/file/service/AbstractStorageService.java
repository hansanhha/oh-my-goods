package co.ohmygoods.file.service;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class AbstractStorageService implements StorageService {

    protected String createDirectoryPath(String baseDirectory, String uploaderEmail) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return baseDirectory + File.separator + currentDate + File.separator + uploaderEmail + File.separator;
    }

    protected String resolveFileName(MultipartFile file) {
        StringBuilder fileName = new StringBuilder(String.valueOf(System.currentTimeMillis()));
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        if (StringUtils.hasText(extension)) {
            fileName.append(".").append(extension);
        }

        return fileName.toString();
    }

    protected String getBaseDirectoryNameByFileType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType != null && !contentType.isBlank()) {
            if (contentType.startsWith("image")) {
                return "images";
            }
        }

        return "files";
    }
}
