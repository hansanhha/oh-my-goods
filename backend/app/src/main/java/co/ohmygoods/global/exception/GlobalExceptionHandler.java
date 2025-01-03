package co.ohmygoods.global.exception;

import co.ohmygoods.global.exception.ProblemDetailResponseEntityBuilder.ProblemDetailInfo;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(DomainException e) {
        ProblemDetailInfo problemDetailInfo = ProblemDetailInfo.builder()
                .exception(e)
                .httpStatusCode(e.getHttpStatus())
                .type(e.getType())
                .title(e.getErrorMessage())
                .errorCode(e.getErrorCode())
                .detail(e.getErrorDetailMessage())
                .instance(e.getInstance())
                .build();

        return ProblemDetailResponseEntityBuilder.build(problemDetailInfo);
    }
}
