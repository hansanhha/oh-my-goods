package co.ohmygoods.payment.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.payment.entity.Payment;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.repository.PaymentRepository;
import co.ohmygoods.payment.vo.ExternalPaymentVendor;
import co.ohmygoods.payment.vo.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class SimplePaymentService implements PaymentService {

    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public Long createPayment(ExternalPaymentVendor externalPaymentVendor, String accountEmail, Long orderId, int paymentAmount, String paymentName) {
        OAuth2Account account = accountRepository.findByEmail(accountEmail).orElseThrow(PaymentException::new);
        Order order = orderRepository.findById(orderId).orElseThrow(PaymentException::new);

        Payment newPayment = Payment.start(account, order, externalPaymentVendor, paymentAmount);

        Payment payment = paymentRepository.save(newPayment);

        return payment.getId();
    }

    @Override
    public Long readyPayment(Long paymentId, String paymentTransactionId, LocalDateTime readyAt) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(PaymentException::new);

        payment.ready(paymentTransactionId, readyAt);

        return paymentId;
    }

    @Override
    public Long successPayment(String orderTransactionId, LocalDateTime succeededAt) {
        Payment payment = paymentRepository.findByOrderTransactionId(orderTransactionId).orElseThrow(PaymentException::new);

        payment.succeed(succeededAt);

        return payment.getId();
    }

    @Override
    public Long cancelPayment(String orderTransactionId, LocalDateTime canceledAt) {
        Payment payment = paymentRepository.findByOrderTransactionId(orderTransactionId).orElseThrow(PaymentException::new);

        payment.cancel(canceledAt);

        return payment.getId();
    }

    @Override
    public Long failPayment(String orderTransactionId, PaymentStatus paymentFailureCause, LocalDateTime failedAt) {
        Payment payment = paymentRepository.findByOrderTransactionId(orderTransactionId).orElseThrow(PaymentException::new);

        payment.fail(paymentFailureCause, failedAt);

        return payment.getId();
    }
}
