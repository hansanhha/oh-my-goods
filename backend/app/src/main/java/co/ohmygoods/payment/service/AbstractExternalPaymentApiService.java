package co.ohmygoods.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

public abstract class AbstractExternalPaymentApiService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected <RequestBody, Response> PreparationResult<Response> sendExternalPreparationRequest(RequestBody preparationRequestBody) {
        var externalApiClient = getExternalApiRestClient();

        return externalApiClient
                .post()
                .uri(getExternalPaymentRequestUri(PaymentPhase.PREPARE))
                .body(preparationRequestBody)
                .exchange((request, response) -> new PreparationResult<>(response));
    }

    protected <RequestBody, Response> ApprovalResult<Response> sendExternalApprovalRequest(RequestBody approvalRequestBody) {
        var externalApiClient = getExternalApiRestClient();

        return externalApiClient
                .post()
                .uri(getExternalPaymentRequestUri(PaymentPhase.APPROVE))
                .body(approvalRequestBody)
                .exchange((request, response) -> new ApprovalResult<>(response));
    }

    protected  <T> Optional<T> convertExternalResponseBodyToFailCause(InputStream responseBody, Class<T> type) {
        try {
            return Optional.of(objectMapper.readValue(responseBody, type));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    protected enum PaymentPhase {
        PREPARE,
        APPROVE
    }

    private static <T> T convertResponse(ConvertibleClientHttpResponse response, ParameterizedTypeReference<T> convertType) {
        return response.bodyTo(convertType);
    }

    @Getter
    protected static class PreparationResult<PreparationResponse> {

        private final PreparationResponse preparationResponse;
        private final boolean success;
        private final HttpStatusCode preparationResponseStatusCode;
        private final InputStream preparationResponseBody;

        private PreparationResult(ConvertibleClientHttpResponse response) throws IOException {
            this.preparationResponseStatusCode = response.getStatusCode();
            this.preparationResponseBody = response.getBody();

            if (response.getStatusCode().isError()) {
                this.preparationResponse = null;
                this.success = false;
                return;
            }

            this.preparationResponse = convertResponse(response, new ParameterizedTypeReference<>() {});
            this.success = true;
        }

    }

    @Getter
    protected static class ApprovalResult<ApprovalResponse> {

        private final ApprovalResponse approvalResponse;
        private final boolean success;
        private final HttpStatusCode approvalResponseStatusCode;
        private final InputStream approvalResponseBody;

        private ApprovalResult(ConvertibleClientHttpResponse response) throws IOException {
            this.approvalResponseStatusCode = response.getStatusCode();
            this.approvalResponseBody = response.getBody();

            if (response.getStatusCode().isError()) {
                this.approvalResponse = null;
                this.success = false;
                return;
            }

            this.approvalResponse = convertResponse(response, new ParameterizedTypeReference<>() {});
            this.success = true;
        }

    }

    abstract protected RestClient getExternalApiRestClient();

    abstract protected URI getExternalPaymentRequestUri(final PaymentPhase paymentPhase);

}
