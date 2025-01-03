package co.ohmygoods.payment.service;

import co.ohmygoods.payment.model.vo.UserAgent;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.service.dto.ExternalApprovalResponse;
import co.ohmygoods.payment.service.dto.ExternalPaymentError;
import co.ohmygoods.payment.service.dto.ExternalPreparationResponse;
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
import java.time.LocalDateTime;
import java.util.Map;

/**
 *
 * <p>
 * 외부 결제 API 요청 처리 클래스
 * 중복되는 로직을 공통화하고 각 구현체마다 다른 부분은 템플릿 메서드로 처리
 * </p>
 *
 * <p>
 * 자식 구현체에서 지정한 제네릭 타입들은 각각 외부 결제 api json 응답 매핑 타입으로
 * ExternalApiRequestResult에서 사용됨
 * </p>
 *
 * {@link ExternalApiRequestResult}
 *
 * @param <PreparationResponse> 외부 결제 준비 API 응답 매핑 타입
 * @param <ApprovalResponse> 외부 결제 승인 API 응답 매핑 타입
 * @param <ExternalError> 외부 결제 API 실패 응답 매핑 타입
 */
public abstract class AbstractPaymentExternalRequestService<PreparationResponse, ApprovalResponse, ExternalError>
        implements PaymentExternalRequestService {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public ExternalPreparationResponse sendPreparationRequest(UserAgent userAgent, String accountEmail, String orderTransactionId, int paymentAmount, String paymentName) {
        Object preparationRequestBody = getPreparationRequestBody(accountEmail, orderTransactionId, paymentAmount, paymentName);

        ExternalApiRequestResult preparationResult = sendExternalPaymentApiRequest(PaymentPhase.PREPARATION, preparationRequestBody);

        PreparationResponseDetail detail = getPreparationResponseDetail(userAgent, preparationResult.getPreparationResponse());

        if (!preparationResult.isSuccess()) {
            return ExternalPreparationResponse.fail(accountEmail, orderTransactionId, paymentAmount,
                    convertToExternalError(preparationResult.getExternalError()), detail.createdAt());
        }

        return ExternalPreparationResponse.success(accountEmail, orderTransactionId,
                detail.externalTransactionId(), detail.nextRedirectURI(), paymentAmount, detail.createdAt(), detail.preparedAt());
    }

    @Override
    public ExternalApprovalResponse sendApprovalRequest(String orderTransactionId, Map<String, String> properties) {
        Object approvalRequestBody = getApprovalRequestBody(orderTransactionId, properties);

        ExternalApiRequestResult approvalResult = sendExternalPaymentApiRequest(PaymentPhase.APPROVAL, approvalRequestBody);

        ApprovalResponseDetail detail = getApprovalResponseDetail(approvalResult.getApprovalResponse());

        if (!approvalResult.isSuccess()) {
            return ExternalApprovalResponse.fail(detail.accountEmail(), orderTransactionId,
                    detail.externalTransactionId(), detail.paymentAmount(),
                    convertToExternalError(approvalResult.getExternalError()), detail.startedAt());
        }

        return ExternalApprovalResponse.success(detail.accountEmail(), orderTransactionId,
                detail.externalTransactionId(), detail.paymentAmount(), detail.startedAt(), detail.approvedAt());
    }

    @SuppressWarnings("unchecked")
    @Override
    public ExternalPaymentError extractExternalPaymentError(Object failureInfo) {
        return convertToExternalError((ExternalError) failureInfo);
    }

    /* -------------------------------------------------------------------
         템플릿 메서드
    ---------------------------------------------------------------------- */

    abstract protected RestClient getExternalApiRestClient();

    abstract protected URI getExternalPaymentRequestUri(final PaymentPhase paymentPhase);

    protected abstract Object getPreparationRequestBody(String accountEmail, String orderTransactionId, int paymentAmount, String paymentName);
    protected abstract Object getApprovalRequestBody(String orderTransactionId, Map<String, String> properties);

    // ExternalPreparationResponse를 생성하기 위한 정보를 외부 결제 준비 api 응답 정보를 바탕으로 자식 구현체에서 추출함
    protected abstract PreparationResponseDetail getPreparationResponseDetail(UserAgent userAgent, PreparationResponse preparationResponse);

    // ExternalApprovalResponse를 생성하기 위한 정보를 외부 결제 승인 api 응답 정보를 바탕으로 자식 구현체에서 추출함
    protected abstract ApprovalResponseDetail getApprovalResponseDetail(ApprovalResponse approvalResponse);

    // 자식 구현체에 종속되는 예외 정보를 ExternalPaymentError 객체로 변환
    protected abstract ExternalPaymentError convertToExternalError(ExternalError externalError);


    /* -------------------------------------------------------------------
         private 메서드
    ---------------------------------------------------------------------- */

    /**
     * <p>외부 결제 api를 요청하는 메서드</p>
     *
     * <p>템플릿 메서드를 통해 api uri와 RestClient를 가져오고
     * 자식 구현체에서 지정한 제네릭 타입을 기반으로 결제 api json 응답을 매핑함</p>
     *
     * <p>외부 결제 api 응답의 http 상태 값에 따라 요청 오류/성공 분기 처리</p>
     * <p>{@link #getFailureResult}
     * {@link #getSuccessResult}</p>
     *
     * @param paymentPhase 결제 단계(준비/승인)
     * @param requestBody 외부 결제 api 요청 바디
     *
     * @return 외부 결제 API 요청(준비/승인) 결과 {@link ExternalApiRequestResult}
     */
    private ExternalApiRequestResult sendExternalPaymentApiRequest(PaymentPhase paymentPhase, Object requestBody) {
        RestClient externalApiClient = getExternalApiRestClient();

        return externalApiClient
                .post()
                .uri(getExternalPaymentRequestUri(paymentPhase))
                .body(convertToJson(requestBody))
                .exchange((request, response) -> {
                    HttpStatusCode externalResponseCode = response.getStatusCode();

                    if (externalResponseCode.isError()) {
                        return getFailureResult(paymentPhase, externalResponseCode, response);
                    }

                    return getSuccessResult(paymentPhase, externalResponseCode, response);
                });
    }

    private byte[] convertToJson(Object requestBody) {
        try {
            return objectMapper.writeValueAsBytes(requestBody);
        } catch (JsonProcessingException e) {
            throw PaymentException.FAILED_PAYMENT_API_REQUEST;
        }
    }

    /**
     * paymentPhase 값에 따라 분기 처리하여 외부 결제 오류 응답 생성
     * <p>{@link #convertToPaymentResponse}</p>
     */
    private ExternalApiRequestResult getFailureResult(PaymentPhase paymentPhase, HttpStatusCode statusCode, ConvertibleClientHttpResponse response) {
        return new ExternalApiRequestResult(null,null,
                false, statusCode, convertToPaymentResponse(response, PaymentPhase.ERROR));
    }

    /**
     * paymentPhase 값에 따라 분기 처리하여 외부 결제 성공 응답 생성
     * <p>{@link #convertToPaymentResponse}</p>
     */
    private ExternalApiRequestResult getSuccessResult(PaymentPhase paymentPhase, HttpStatusCode statusCode, ConvertibleClientHttpResponse response) {
        if (paymentPhase.equals(PaymentPhase.PREPARATION)) {
            return new ExternalApiRequestResult(convertToPaymentResponse(response, paymentPhase), null, true,
                    statusCode, null);
        }

        return new ExternalApiRequestResult(null, convertToPaymentResponse(response, PaymentPhase.APPROVAL), true,
                statusCode, null);
    }

    /**
     * <p>외부 결제 api 응답을 자식 구현체에서 지정한 타입으로 변환하는 메서드</p>
     * <p>제네릭 타입은 런타임에 소거되지만 클래스의 Type 정보는 유지되므로 이 정보를 바탕으로 제네릭 타입을 추출하여
     * 해당 타입으로 외부 결제 api 응답을 변환함</p>
     *
     * @param response 외부 결제 api json 응답
     * @param paymentPhase 현재 결제 단계(준비/승인/에러), 이 값에 따라 변환할 제네릭 타입 선택
     * @param <T> 자식 구현체에서 지정한 제네릭 타입
     * @return 자식 구현체에서 지정한 제네릭 타입으로 변환된 외부 결제 api 응답
     */
    @SuppressWarnings("unchecked")
    private <T> T convertToPaymentResponse(ConvertibleClientHttpResponse response, PaymentPhase paymentPhase) {
        Type declaredGenericTypes = this.getClass().getGenericSuperclass();

        if (declaredGenericTypes instanceof ParameterizedType declaredParameterizedType) {

            Type targetType = switch (paymentPhase) {
                case PREPARATION -> declaredParameterizedType.getActualTypeArguments()[0];
                case APPROVAL -> declaredParameterizedType.getActualTypeArguments()[1];
                case ERROR -> declaredParameterizedType.getActualTypeArguments()[2];
            };

            TypeReference<Object> targetTypeReference = new TypeReference<>() {
                @Override
                public Type getType() {
                    return targetType;
                }
            };

            return (T) convertExternalApiResponseToTargetTypeReference(response, targetTypeReference);
        }

        throw new IllegalStateException();
    }

    private <T> T convertExternalApiResponseToTargetTypeReference(ConvertibleClientHttpResponse response, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(response.getBody(), typeReference);
        } catch (IOException e) {
            return null;
        }
    }

    /* -----------------------------------------------------------
         protected 멤버 클래스
    ----------------------------------------------------------- */

    protected enum PaymentPhase {
        PREPARATION,
        APPROVAL,
        ERROR
    }

    // 결제 준비 api 응답 상세 정보
    // ExternalPreparationResponse 객체를 생성하기 위한 필요 정보
    protected record PreparationResponseDetail(String externalTransactionId,
                                               String nextRedirectURI,
                                               LocalDateTime createdAt,
                                               LocalDateTime preparedAt) {
    }

    // 결제 승인 api 응답 상세 정보
    // ExternalApprovalResponse 객체를 생성하기 위한 필요 정보
    protected record ApprovalResponseDetail(String accountEmail,
                                            String externalTransactionId,
                                            int paymentAmount,
                                            LocalDateTime startedAt,
                                            LocalDateTime approvedAt) {
    }

    /* -----------------------------------------------------------
         private  멤버 클래스
         외부 api 요청 결과에 대한 매핑 클래스로
         AbstractExternalPaymentApiService 내부에서만 사용됨
    ----------------------------------------------------------- */

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private class ExternalApiRequestResult {
        PreparationResponse preparationResponse;
        ApprovalResponse approvalResponse;
        boolean success;
        HttpStatusCode externalHttpStatusCode;
        ExternalError externalError;
    }

}
