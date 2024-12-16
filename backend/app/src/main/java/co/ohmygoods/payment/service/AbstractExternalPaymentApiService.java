package co.ohmygoods.payment.service;

import co.ohmygoods.payment.exception.PaymentException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;

/**
 *
 * 외부 결제 API 요청 처리 클래스
 * 중복되는 로직을 공통화하고 각 구현체마다 다른 부분은 템플릿 메서드로 처리
 *
 * @param <PreparationResponse> 외부 결제 준비 API 응답 매핑 타입
 * @param <ApprovalResponse> 외부 결제 승인 API 응답 매핑 타입
 * @param <ExternalError> 외부 결제 API 실패 응답 매핑 타입
 */
public abstract class AbstractExternalPaymentApiService<PreparationResponse, ApprovalResponse, ExternalError> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * @param <RequestBody> 외부 결제 준비 API 요청 HTTP 바디 타입
     *
     * @param preparationRequestBody 외부 결제 준비 API 요청 HTTP 바디
     *
     * @return 외부 결제 준비 API 요청 결과
     */
    protected <RequestBody> PreparationResult sendExternalPaymentPreparationRequest(RequestBody preparationRequestBody) {
        var externalApiClient = getExternalApiRestClient();

        return externalApiClient
                .post()
                .uri(getExternalPaymentRequestUri(PaymentPhase.PREPARATION))
                .body(convertRequestBodyToJson(preparationRequestBody))
                .exchange((request, response) -> {
                    HttpStatusCode externalResponseCode = response.getStatusCode();
                    if (externalResponseCode.isError()) {
                        return new PreparationResult(null, false, externalResponseCode, convertToPaymentResponse(response, PaymentPhase.ERROR));
                    }

                    return new PreparationResult(convertToPaymentResponse(response, PaymentPhase.PREPARATION), true, externalResponseCode, null);
                });
    }

    /**
     *
     * @param <RequestBody> 외부 결제 승인 API 요청 HTTP 바디 타입
     *
     * @param approvalRequestBody 외부 결제 승인 API 요청 HTTP 바디
     *
     * @return 외부 결제 승인 API 요청 결과
     */
    protected <RequestBody> ApprovalResult sendExternalPaymentApprovalRequest(RequestBody approvalRequestBody) {
        var externalApiClient = getExternalApiRestClient();

        return externalApiClient
                .post()
                .uri(getExternalPaymentRequestUri(PaymentPhase.APPROVAL))
                .body(convertRequestBodyToJson(approvalRequestBody))
                .exchange((request, response) -> {
                    if (response.getStatusCode().isError()) {
                        return new ApprovalResult(null, false, response.getStatusCode(), convertToPaymentResponse(response, PaymentPhase.ERROR));
                    }

                    return new ApprovalResult(convertToPaymentResponse(response, PaymentPhase.APPROVAL), true, response.getStatusCode(), null);
                });
    }

    /* -------------------------------------------------------------------
         HTTP 요청, 응답을 자식 클래스에서 지정한 타입으로 변환하는 private 메서드
    ---------------------------------------------------------------------- */

    private <T> byte[] convertRequestBodyToJson(T requestBody) {
        try {
            return objectMapper.writeValueAsBytes(requestBody);
        } catch (JsonProcessingException e) {
            throw PaymentException.invalidExternalRequestBody();
        }
    }

    /**
     * <p>외부 결제 api 응답을 자식 구현체에서 지정한 타입으로 변환하는 메서드</p>
     * <p>제네릭 타입은 런타임에 소거되지만 클래스의 Type 정보는 유지되므로 이 정보를 바탕으로 제네릭 타입을 추출할 수 있음</p>
     *
     * @param response 외부 결제 api json 응답
     * @param paymentPhase 현재 결제 단계(준비/승인/에러), 이 값에 따라 변환할 제네릭 타입 선택
     * @param <T> 자식 구현체에서 지정한 제네릭 타입으로 변환된 외부 결제 api 응답 제네릭 타입
     * @return 자식 구현체에서 지정한 제네릭 타입으로 변환된 외부 결제 api 응답
     */
    @SuppressWarnings("unchecked")
    private <T> T convertToPaymentResponse(ConvertibleClientHttpResponse response, PaymentPhase paymentPhase) {
        Type declaredGenericTypes = this.getClass().getGenericSuperclass();

        if (declaredGenericTypes instanceof ParameterizedType declaredParameterizedType) {

            Type responseType = switch (paymentPhase) {
                case PREPARATION -> declaredParameterizedType.getActualTypeArguments()[0];
                case APPROVAL -> declaredParameterizedType.getActualTypeArguments()[1];
                case ERROR -> declaredParameterizedType.getActualTypeArguments()[2];
            };

            TypeReference<Object> convertTypeReference = new TypeReference<>() {
                @Override
                public Type getType() {
                    return responseType;
                }
            };

            return (T) convertResponseToTypeReference(response, convertTypeReference);
        }

        throw new IllegalStateException();
    }

    private <T> T convertResponseToTypeReference(ConvertibleClientHttpResponse response, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(response.getBody(), typeReference);
        } catch (IOException e) {
            return null;
        }
    }

    /* -----------------------------------------------------------
         자식 구현체에서 구현해야 될 템플릿 메서드
    ----------------------------------------------------------- */

    abstract protected RestClient getExternalApiRestClient();

    abstract protected URI getExternalPaymentRequestUri(final PaymentPhase paymentPhase);

    /* -----------------------------------------------------------
         멤버 클래스(결제 단계 명시, 결제 API 요청 결과 DTO)
    ----------------------------------------------------------- */

    protected enum PaymentPhase {
        PREPARATION,
        APPROVAL,
        ERROR
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    protected class PreparationResult {
        PreparationResponse preparationResponse;
        boolean success;
        HttpStatusCode externalHttpStatusCode;
        ExternalError externalError;

    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    protected class ApprovalResult {
        ApprovalResponse approvalResponse;
        boolean success;
        HttpStatusCode externalHttpStatusCode;
        ExternalError externalError;

    }

}
