package co.ohmygoods.payment.controller;

import co.ohmygoods.payment.controller.dto.KakaopayApproveFailureDto;
import co.ohmygoods.payment.service.dto.ApprovePaymentRequest;
import co.ohmygoods.payment.service.PaymentGateway;
import co.ohmygoods.payment.model.vo.ExternalPaymentVendor;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Hidden
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentGateway paymentGateway;

    @PostMapping("/kakao/approve")
    public void processKakaopayApproval(@RequestParam("orderNumber") String orderNumber,
                                        @RequestParam("pg_token") String pgToken) {
        paymentGateway.continuePayment(new ApprovePaymentRequest(ExternalPaymentVendor.KAKAOPAY, orderNumber, Map.of("pg_token", pgToken)));
    }

    @PostMapping("/kakao/cancel")
    public void processKakaopayCancel(@RequestParam("orderNumber") String orderNumber) {
        paymentGateway.cancelPayment(ExternalPaymentVendor.KAKAOPAY, orderNumber);
    }

    @PostMapping("/kakao/fail")
    public void processKakaopayFail(@RequestParam("orderNumber") String orderNumber,
                                    @RequestBody KakaopayApproveFailureDto failureDto) {
        paymentGateway.failPayment(ExternalPaymentVendor.KAKAOPAY, orderNumber, failureDto);
    }


}
