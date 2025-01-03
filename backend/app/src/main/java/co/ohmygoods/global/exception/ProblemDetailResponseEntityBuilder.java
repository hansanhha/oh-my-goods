package co.ohmygoods.global.exception;

import lombok.Builder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Objects;

/**
 * <p>
 * application/problem+json 타입의 응답 객체 생성 유틸 클래스로
 * 주어진 ProblemDetailInfo를 통해 ProblemDetail을 생성한 뒤 ResponseEntity 반환
 * </p>
 *
 * ProblemDetailInfo 필수값: HttpHeaders, HttpStatusCode, detail(String)
 */
public class ProblemDetailResponseEntityBuilder {

    public static ResponseEntity<ProblemDetail> build(ProblemDetailInfo problemDetailInfo) {
        ProblemDetail problemDetail = createProblemDetail(problemDetailInfo);
        return new ResponseEntity<>(problemDetail, problemDetailInfo.httpHeaders, problemDetailInfo.httpStatusCode);
    }

    private static ProblemDetail createProblemDetail(ProblemDetailInfo problemDetailInfo) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(problemDetailInfo.httpStatusCode, problemDetailInfo.detail);

        if (Objects.nonNull(problemDetailInfo.type)) {
            problemDetail.setType(problemDetailInfo.type);
        }

        if (StringUtils.hasText(problemDetailInfo.errorCode)) {
            problemDetail.setProperty("errorCode", problemDetailInfo.errorCode);
        }

        if (StringUtils.hasText(problemDetailInfo.title)) {
            problemDetail.setTitle(problemDetailInfo.title);
        }

        if (Objects.nonNull(problemDetailInfo.instance)) {
            problemDetail.setInstance(problemDetailInfo.instance);
        }

        return problemDetail;
    }

    @Builder
    public static class ProblemDetailInfo {
        private final Exception exception;
        private final HttpHeaders httpHeaders;
        private final HttpStatusCode httpStatusCode;
        private final URI type;
        private final String errorCode;
        private final String title;
        private final String detail;
        private final URI instance;
    }
}
