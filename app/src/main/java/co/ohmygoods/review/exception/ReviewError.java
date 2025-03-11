package co.ohmygoods.review.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReviewError implements DomainError {

    NOT_FOUND_REVIEW_IMAGE(HttpStatus.NOT_FOUND, "R000", "리뷰 이미지를 찾을 수 없습니다."),
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "R001", "리뷰를 찾을 수 없습니다."),
    NOT_FOUND_REVIEW_COMMENT(HttpStatus.NOT_FOUND, "R002", "리뷰 댓글을 찾을 수 없습니다."),
    NOT_FOUND_REVIEW_REPLY(HttpStatus.NOT_FOUND, "R003", "리뷰 답글을 찾을 수 없습니다."),

    INVALID_REVIEW_SCORE(HttpStatus.BAD_REQUEST, "R100", "유효하지 않은 리뷰 점수입니다"),
    INVALID_REVIEW_CONTENT(HttpStatus.BAD_REQUEST, "R101", "유효하지 않은 리뷰 내용입니다"),
    INVALID_REVIEW_STATUS(HttpStatus.BAD_REQUEST, "R102", "유효하지 않은 리뷰 상태입니다"),

    INVALID_AUTHORITY_REVIEW(HttpStatus.BAD_REQUEST, "R200", "리뷰 권한이 없습니다."),
    INVALID_AUTHORITY_REVIEW_COMMENT(HttpStatus.BAD_REQUEST, "R201", "리뷰 댓글 권한이 없습니다."),
    INVALID_AUTHORITY_REVIEW_REPLY(HttpStatus.BAD_REQUEST, "R202", "리뷰 답변 권한이 없습니다."),
    ALREADY_WRITTEN_REVIEW(HttpStatus.BAD_REQUEST, "R201", "이미 작성한 리뷰가 있습니다.");

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
