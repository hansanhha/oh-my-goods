package co.ohmygoods.order.service;

import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.payment.service.PaymentProcessListener;
import co.ohmygoods.payment.model.vo.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderPaymentProcessListener implements PaymentProcessListener {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Override
    public void onSuccess(Long paymentId) {
        Order order = orderRepository.findByPaymentId(paymentId).orElseThrow(OrderException::new);

        orderService.successOrder(order.getId());
    }

    @Override
    public void onCancel(Long paymentId) {
        Order order = orderRepository.findByPaymentId(paymentId).orElseThrow(OrderException::new);

        orderService.cancelOrderByPaymentCancellation(order.getId());
    }

    @Override
    public void onFailure(Long paymentId, PaymentStatus paymentFailureCause) {
        Order order = orderRepository.findByPaymentId(paymentId).orElseThrow(OrderException::new);

        orderService.failOrderByPaymentFailed(order.getId(), paymentFailureCause);
    }
}
