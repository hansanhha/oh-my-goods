package co.ohmygoods.payment.service;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.payment.model.entity.Payment;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.model.vo.PaymentAPIProvider;
import co.ohmygoods.payment.repository.PaymentRepository;
import co.ohmygoods.payment.model.vo.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultPaymentService implements PaymentService {

    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public Long createPayment(PaymentAPIProvider paymentAPIProvider, String accountEmail, Long orderId, int paymentAmount, String paymentName) {
        Account account = accountRepository.findByEmail(accountEmail).orElseThrow(AuthException::notFoundAccount);
        Order order = orderRepository.findById(orderId).orElseThrow(OrderException::notFoundOrder);

        Payment newPayment = Payment.start(account, order, paymentAPIProvider, paymentAmount);

        Payment payment = paymentRepository.save(newPayment);

        return payment.getId();
    }

    @Override
    public Long readyPayment(Long paymentId, String paymentTransactionId, LocalDateTime readyAt) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(PaymentException::notFoundPayment);

        payment.ready(paymentTransactionId, readyAt);

        return paymentId;
    }

    @Override
    public Long successPayment(String orderTransactionId, LocalDateTime succeededAt) {
        Payment payment = paymentRepository.findByOrderTransactionId(orderTransactionId).orElseThrow(PaymentException::notFoundPayment);

        payment.succeed(succeededAt);

        return payment.getId();
    }

    @Override
    public Long cancelPayment(String orderTransactionId, LocalDateTime canceledAt) {
        Payment payment = paymentRepository.findByOrderTransactionId(orderTransactionId).orElseThrow(PaymentException::notFoundPayment);

        payment.cancel(canceledAt);

        return payment.getId();
    }

    @Override
    public Long failPayment(String orderTransactionId, PaymentStatus paymentFailureCause, LocalDateTime failedAt) {
        Payment payment = paymentRepository.findByOrderTransactionId(orderTransactionId).orElseThrow(PaymentException::notFoundPayment);

        payment.fail(paymentFailureCause, failedAt);

        return payment.getId();
    }
}
