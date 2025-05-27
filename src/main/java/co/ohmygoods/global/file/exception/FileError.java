package co.ohmygoods.global.file.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FileError implements DomainError {

    NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "F001", "파일을 찾을 수 없습니다."),
    EMPTY_UPLOAD_FILE(HttpStatus.BAD_REQUEST, "F100", "업로드할 파일이 없습니다"),

    FAILED_CREATE_DIRECTORY(HttpStatus.INTERNAL_SERVER_ERROR, "F002", "디렉토리 생성에 실패했습니다"),
    FAILED_CREATE_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "F003", "파일 생성에 실패했습니다."),
    FAILED_FILE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "F004", "파일 업로드에 실패했습니다."),
    FAILED_FILE_DOWNLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "F005", "파일 다운로드에 실패했습니다."),

    INVALID_FILE(HttpStatus.BAD_REQUEST, "F006", "유효하지 않은 파일입니다."),
    INVALID_CLOUD_PROVIDER(HttpStatus.BAD_REQUEST, "F007", "유효하지 않은 클라우드 제공자입니다.");


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
