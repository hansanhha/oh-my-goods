package co.ohmygoods.payment.controller.dto;

public record KakaopayApproveFailureDto(String errorCode,
                                        String errorMessage,
                                        DetailFailureInfo extras) {

    public record DetailFailureInfo(String methodResultCode,
                                    String methodResultMessage) {
    }
}
