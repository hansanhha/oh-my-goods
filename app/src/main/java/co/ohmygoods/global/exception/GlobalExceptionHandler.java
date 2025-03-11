package co.ohmygoods.global.exception;

import co.ohmygoods.global.exception.ProblemDetailResponseEntityBuilder.ProblemDetailInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(DomainException e) {

        logDomainException(e);

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

    // 컨트롤러 bean validation 실패 처리
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BindingResult bindingResult = e.getBindingResult();

        Map<String, String> errors = new HashMap<>(bindingResult.getFieldErrorCount());

        bindingResult.getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

        return handleExceptionInternal(e, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    private void logDomainException(DomainException e) {
        if (e.isServerError()) {
            log.error("occurred {}. detail: {} ", e.getClass().getSimpleName(), e.toString());
            return;
        }

        log.info("occurred {}. detail: {} ", e.getClass().getSimpleName(), e.toString());
    }

}
