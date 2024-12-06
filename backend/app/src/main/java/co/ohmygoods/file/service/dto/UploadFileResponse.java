package co.ohmygoods.file.service.dto;

public record UploadFileResponse(String uploadedFileName,
                                 String uploadedFileContentType,
                                 String uploadedFilePath) {
}
