package co.ohmygoods.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.net.URI;

public abstract class DomainException extends RuntimeException {

    private final DomainError domainError;
    private final DomainErrorAdditionalAttributes additionalAttributes;

    public DomainException(DomainError domainError) {
        this(domainError, null);
    }

    public DomainException(DomainError domainError, URI type, URI instance) {
        this(domainError, new DomainErrorAdditionalAttributes(type, instance));
    }

    private DomainException(DomainError domainError, DomainErrorAdditionalAttributes additionalAttributes) {
        super(domainError.getErrorDetailMessage() + ", exception source: " + Thread.currentThread().getStackTrace()[5]);
        this.domainError = domainError;
        this.additionalAttributes = additionalAttributes;
        setStackTrace(new StackTraceElement[]{Thread.currentThread().getStackTrace()[5]});
    }

    public boolean isServerError() {
        return domainError.getHttpStatus().is5xxServerError();
    }

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

    @Override
    public String toString() {
        URI instance = getInstance();

        return """
               exception: %s
               instance: %s
               response status: %s
               error detail: %s
               
               """.formatted(
                        this.getClass().getSimpleName(),
                        instance == null ? "none" : instance,
                        domainError.getHttpStatus(),
                        domainError.getErrorDetailMessage());
    }
}
