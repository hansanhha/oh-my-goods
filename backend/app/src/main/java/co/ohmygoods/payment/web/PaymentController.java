package co.ohmygoods.payment.web;

import co.ohmygoods.payment.service.PaymentGateway;
import co.ohmygoods.payment.vo.PaymentVendor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentGateway paymentGateway;

    @PostMapping("/kakao/approve")
    public void processKakaopayApproval(@RequestParam("pg_token") String pgToken) {
//        paymentGateway.approvePayment(PaymentVendor.KAKAOPAY, );
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
