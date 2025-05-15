package co.ohmygoods.order.service;

import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.entity.OrderItem;
import co.ohmygoods.order.repository.OrderItemRepository;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.order.service.dto.OrderItemDetailResponse;
import co.ohmygoods.order.service.dto.OrderItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderManagementService {

    private final OrderItemRepository orderItemRepository;

    public Slice<OrderItemResponse> getOrders(String memberId, Pageable pageable) {
        Slice<OrderItem> orderItems = orderItemRepository.findAllByOrderAccountMemberId(memberId, pageable);

        return orderItems.map(OrderItemResponse::from);
    }

    public OrderItemDetailResponse getOrder(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.fetchById(orderItemId).orElseThrow(OrderException::notFoundOrder);

        return OrderItemDetailResponse.from(orderItem);
    }
}
