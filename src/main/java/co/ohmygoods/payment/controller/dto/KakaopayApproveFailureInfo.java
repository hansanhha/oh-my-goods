package co.ohmygoods.payment.controller.dto;

public record KakaopayApproveFailureInfo(String errorCode,
                                         String errorMessage,
                                         DetailFailureInfo extras) {

    public record DetailFailureInfo(String methodResultCode,
                                    String methodResultMessage) {
    }
}
