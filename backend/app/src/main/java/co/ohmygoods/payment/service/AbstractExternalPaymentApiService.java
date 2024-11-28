package co.ohmygoods.payment.service;

import co.ohmygoods.payment.exception.PaymentException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;

import java.io.IOException;
import java.net.URI;

/**
 *
 * @param <PreparationResponse> 외부 결제 준비 API 응답 매핑 타입
 * @param <ApprovalResponse> 외부 결제 승인 API 응답 매핑 타입
 * @param <Error> 외부 결제 API 실패 응답 매핑 타입
 */
public abstract class AbstractExternalPaymentApiService<PreparationResponse, ApprovalResponse, Error> {

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
    protected <RequestBody> PreparationResult<PreparationResponse, Error> sendExternalPaymentPreparationRequest(RequestBody preparationRequestBody) {
        var externalApiClient = getExternalApiRestClient();

        return externalApiClient
                .post()
                .uri(getExternalPaymentRequestUri(PaymentPhase.PREPARATION))
                .body(convertRequestBodyToJson(preparationRequestBody))
                .exchange((request, response) -> {
                    HttpStatusCode externalResponseCode = response.getStatusCode();
                    if (externalResponseCode.isError()) {
                        return PreparationResult.error(externalResponseCode, extractExternalFailureCause(response, getExternalErrorMappingTypeReference()));
                    }

                    return PreparationResult.success(externalResponseCode, convertToResponse(response, getPreprationResponseMappingTypeReference()));
                });
    }

    /**
     *
     * @param <T> 외부 결제 승인 API 요청 HTTP 바디 타입
     *
     * @param approvalRequestBody 외부 결제 승인 API 요청 HTTP 바디
     *
     * @return 외부 결제 승인 API 요청 결과
     */
    protected <T> ApprovalResult<ApprovalResponse, Error> sendExternalPaymentApprovalRequest(T approvalRequestBody) {
        var externalApiClient = getExternalApiRestClient();

        return externalApiClient
                .post()
                .uri(getExternalPaymentRequestUri(PaymentPhase.APPROVAL))
                .body(convertRequestBodyToJson(approvalRequestBody))
                .exchange((request, response) -> {
                    if (response.getStatusCode().isError()) {
                        return new ApprovalResult<>(null, false, response.getStatusCode(), extractExternalFailureCause(response, getExternalErrorMappingTypeReference()));
                    }

                    return new ApprovalResult<>(convertToResponse(response, getApprovalResponseMappingTypeReference()), true, response.getStatusCode(), null);
                });
    }

    abstract protected RestClient getExternalApiRestClient();

    abstract protected URI getExternalPaymentRequestUri(final PaymentPhase paymentPhase);

    abstract protected TypeReference<PreparationResponse> getPreprationResponseMappingTypeReference();

    abstract protected TypeReference<ApprovalResponse> getApprovalResponseMappingTypeReference();

    abstract protected TypeReference<Error> getExternalErrorMappingTypeReference();

    protected enum PaymentPhase {
        PREPARATION,
        APPROVAL
    }

    protected record PreparationResult<PreparationResponse, ExternalError>(PreparationResponse preparationResponse,
                                                                           boolean success,
                                                                           HttpStatusCode externalHttpStatusCode,
                                                                           ExternalError externalError) {

        private static <PreparationResponse, ExternalError> PreparationResult<PreparationResponse, ExternalError> error(HttpStatusCode externalHttpStatusCode, ExternalError externalError) {
            return new PreparationResult<>(null, false, externalHttpStatusCode, externalError);
        }

        private static <PreparationResponse, ExternalError> PreparationResult<PreparationResponse, ExternalError> success(HttpStatusCode externalHttpStatusCode, PreparationResponse preparationResponse) {
            return new PreparationResult<>(preparationResponse, true, externalHttpStatusCode, null);
        }
    }

    protected record ApprovalResult<ApprovalResponse, ExternalError>(ApprovalResponse approvalResponse, boolean success,
                                                                     HttpStatusCode externalHttpStatusCode,
                                                                     ExternalError externalError) {

    }

    private <T> byte[] convertRequestBodyToJson(T requestBody) {
        try {
            return objectMapper.writeValueAsBytes(requestBody);
        } catch (JsonProcessingException e) {
            throw PaymentException.invalidExternalRequestBody();
        }
    }

    private <T> T convertToResponse(ConvertibleClientHttpResponse response, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(response.getBody(), typeReference);
        } catch (IOException e) {
            return null;
        }
    }

    private <T> T extractExternalFailureCause(HttpInputMessage response, TypeReference<T> externalErrorTypeReference) {
        try {
            return objectMapper.readValue(response.getBody(), externalErrorTypeReference);
        } catch (IOException e) {
            return null;
        }
    }

}
