package co.ohmygoods.global.exception;

import org.springframework.http.HttpStatus;

public interface DomainError {

    HttpStatus getHttpStatus();
    String getErrorCode();
    String getErrorMessage();

    default String getErrorDetailMessage() {
        return "[code: " + getErrorCode() + ", message: " + getErrorMessage() + "]";
    }
}
