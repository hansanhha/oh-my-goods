package co.ohmygoods.global.exception;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.net.URI;

@RequiredArgsConstructor
@AllArgsConstructor
public abstract class DomainException extends RuntimeException {

    private final DomainError domainError;
    private DomainErrorAdditionalAttributes additionalAttributes;

    public HttpStatus getHttpStatus() {
        return domainError.getHttpStatus();
    }

    public String getErrorCode() {
        return domainError.getErrorCode();
    }

    public String getErrorMessage() {
        return domainError.getHttpStatus().getReasonPhrase();
    }

    public String getErrorDetailMessage() {
        return domainError.getErrorMessage();
    }

    @Nullable
    public URI getType() {
        if (additionalAttributes == null || additionalAttributes.type() == null) {
            return null;
        }

        return additionalAttributes.type();
    }

    @Nullable
    public URI getInstance() {
        if (additionalAttributes == null || additionalAttributes.instance() == null) {
            return null;
        }

        return additionalAttributes.instance();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
