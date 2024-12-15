package co.ohmygoods.order.service;

import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.model.entity.OrderItem;
import co.ohmygoods.order.model.vo.OrderStatus;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.payment.service.PaymentResultListener;
import co.ohmygoods.payment.vo.PaymentStatus;
import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.exception.ProductStockStatusException;
import co.ohmygoods.product.model.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderPaymentResultListener implements PaymentResultListener {

    private final OrderRepository orderRepository;

    @Override
    public void onSuccess(Long paymentId, Long orderId) {
        Order order = orderRepository.fetchOrderItemsAndProductById(orderId).orElseThrow(OrderException::new);

        List<OrderItem> orderItems = order.getOrderItems();

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            int orderQuantity = orderItem.getOrderQuantity();

            // 구매 수량만큼 재고 차감
            // 예외 발생 시 주문 실패 처리(트랜잭션 예외 발생 X) -> 결제 금액 환불 처리 필요
            try {
                product.decrease(orderQuantity);
            } catch (ProductException e) {
                order.fail(OrderStatus.ORDER_FAILED_OUT_OF_STOCK, PaymentStatus.PAID);
            } catch (ProductStockStatusException e) {
                order.fail(OrderStatus.ORDER_FAILED_INVALID_PRODUCT_STOCK_STATUS, PaymentStatus.PAID);
            }
        }

        order.ordered();
    }

    @Override
    public void onCancel(Long paymentId, Long orderId) {

    }

    @Override
    public void onFailure(Long paymentId, Long orderId, PaymentStatus failureCause) {

    }
}
