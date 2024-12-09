package co.ohmygoods.file.service.dto;

public record UploadFileResponse(String uploadedDomainId,
                                 String uploadedFileName,
                                 String uploadedFileContentType,
                                 String uploadedFilePath) {
}
