package co.ohmygoods.global.file.service.dto;

public record UploadFileResponse(String uploadedDomainId,
                                 String uploadedFileName,
                                 String uploadedFileContentType,
                                 String uploadedFilePath) {
}
