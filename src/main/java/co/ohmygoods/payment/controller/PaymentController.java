package co.ohmygoods.payment.controller;

import co.ohmygoods.payment.controller.dto.KakaopayApproveFailureInfo;
import co.ohmygoods.payment.model.vo.PaymentAPIProvider;
import co.ohmygoods.payment.service.dto.PaymentApproveAPIRequest;
import co.ohmygoods.payment.service.PaymentGateway;
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
        paymentGateway.complete(new PaymentApproveAPIRequest(PaymentAPIProvider.KAKAOPAY, orderNumber, Map.of("pg_token", pgToken)));
    }

    @PostMapping("/kakao/cancel")
    public void processKakaopayCancel(@RequestParam("orderNumber") String orderNumber) {
        paymentGateway.cancelPayment(PaymentAPIProvider.KAKAOPAY, orderNumber);
    }

    @PostMapping("/kakao/fail")
    public void processKakaopayFail(@RequestParam("orderNumber") String orderNumber,
                                    @RequestBody KakaopayApproveFailureInfo failureInfo) {
        paymentGateway.failPayment(PaymentAPIProvider.KAKAOPAY, orderNumber, failureInfo.errorCode(), failureInfo.errorMessage());
    }


}
