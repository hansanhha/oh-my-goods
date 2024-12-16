package co.ohmygoods.payment.web;

import co.ohmygoods.payment.service.dto.ApprovePaymentRequest;
import co.ohmygoods.payment.service.PaymentGateway;
import co.ohmygoods.payment.vo.ExternalPaymentVendor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public void processKakaopayCancel() {
//        paymentGateway.cancelPayment(PaymentVendor.KAKAOPAY, );
    }

    @PostMapping("/kakao/fail")
    public void processKakaopayFail() {
//        paymentGateway.failPayment(PaymentVendor.KAKAOPAY, );
    }


}
