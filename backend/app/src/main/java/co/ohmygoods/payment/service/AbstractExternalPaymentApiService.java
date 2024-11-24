package co.ohmygoods.payment.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

public abstract class AbstractExternalPaymentApiService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected <RequestBody, Response, ExternalError> PreparationResult<Response, ExternalError> sendExternalPaymentPreparationRequest(RequestBody preparationRequestBody) {
        var externalApiClient = getExternalApiRestClient();

        return externalApiClient
                .post()
                .uri(getExternalPaymentRequestUri(PaymentPhase.PREPARATION))
                .body(preparationRequestBody)
                .exchange((request, response) -> new PreparationResult<>(response));
    }

    protected <RequestBody, Response, ExternalError> ApprovalResult<Response, ExternalError> sendExternalPaymentApprovalRequest(RequestBody approvalRequestBody) {
        var externalApiClient = getExternalApiRestClient();

        return externalApiClient
                .post()
                .uri(getExternalPaymentRequestUri(PaymentPhase.APPROVAL))
                .body(approvalRequestBody)
                .exchange((request, response) -> new ApprovalResult<>(response));
    }

    protected static <T> Optional<T> extractExternalFailureCause(HttpInputMessage response, TypeReference<T> type) {
        try {
            return Optional.of(objectMapper.readValue(response.getBody(), type));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    protected enum PaymentPhase {
        PREPARATION,
        APPROVAL
    }

    private static <T> T convertResponse(ConvertibleClientHttpResponse response, ParameterizedTypeReference<T> convertType) {
        return response.bodyTo(convertType);
    }

    @Getter
    protected static class PreparationResult<PreparationResponse, ExternalError> {

        private final PreparationResponse preparationResponse;
        private final boolean success;
        private final HttpStatusCode preparationResponseStatusCode;
        private final ExternalError externalError;

        private PreparationResult(ConvertibleClientHttpResponse response) throws IOException {
            this.preparationResponseStatusCode = response.getStatusCode();

            if (response.getStatusCode().isError()) {
                this.preparationResponse = null;
                this.success = false;
                this.externalError = extractExternalFailureCause(response,
                        new TypeReference<ExternalError> (){}).orElse(null);
                return;
            }

            this.preparationResponse = convertResponse(response, new ParameterizedTypeReference<>() {});
            this.success = true;
            this.externalError = null;
        }

    }

    @Getter
    protected static class ApprovalResult<ApprovalResponse, ExternalError> {

        private final ApprovalResponse approvalResponse;
        private final boolean success;
        private final HttpStatusCode approvalResponseStatusCode;
        private final ExternalError externalError;

        private ApprovalResult(ConvertibleClientHttpResponse response) throws IOException {
            this.approvalResponseStatusCode = response.getStatusCode();

            if (response.getStatusCode().isError()) {
                this.approvalResponse = null;
                this.success = false;
                this.externalError = extractExternalFailureCause(response,
                        new TypeReference<ExternalError> (){}).orElse(null);
                return;
            }

            this.approvalResponse = convertResponse(response, new ParameterizedTypeReference<>() {});
            this.success = true;
            this.externalError = null;
        }

    }

    abstract protected RestClient getExternalApiRestClient();

    abstract protected URI getExternalPaymentRequestUri(final PaymentPhase paymentPhase);

}
