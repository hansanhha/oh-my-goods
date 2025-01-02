package co.ohmygoods.order.controller;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.order.controller.dto.OrderCheckoutWebRequest;
import co.ohmygoods.order.service.OrderManagementService;
import co.ohmygoods.order.service.OrderTransactionService;
import co.ohmygoods.order.service.dto.OrderCheckoutRequest;
import co.ohmygoods.order.service.dto.OrderCheckoutResponse;
import co.ohmygoods.order.service.dto.OrderItemDetailResponse;
import co.ohmygoods.order.service.dto.OrderItemResponse;
import co.ohmygoods.payment.model.vo.ExternalPaymentVendor;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderManagementService orderManagementService;
    private final OrderTransactionService orderTransactionService;

    @GetMapping
    public ResponseEntity<?> getOrders(@AuthenticationPrincipal AuthenticatedAccount account,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Slice<OrderItemResponse> orders = orderManagementService.getOrders(account.memberId(), Pageable.ofSize(size).withPage(page));
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderItemId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderItemId) {
        OrderItemDetailResponse order = orderManagementService.getOrder(orderItemId);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<?> checkout(@AuthenticationPrincipal AuthenticatedAccount account,
                                      @RequestBody OrderCheckoutWebRequest request) {

        List<OrderCheckoutRequest.OrderProductDetail> orderProductDetails = request.orderDetails().stream()
                .map(detail -> new OrderCheckoutRequest.OrderProductDetail(detail.productId(),
                        detail.PurchaseQuantity(), detail.isAppliedCoupon(), detail.appliedCouponId()))
                .toList();

        OrderCheckoutRequest orderCheckoutRequest = new OrderCheckoutRequest(account.memberId(), orderProductDetails,
                ExternalPaymentVendor.valueOf(request.orderPaymentMethod().toUpperCase()), request.deliveryAddressId(), request.totalOrderPrice());

        OrderCheckoutResponse checkoutResponse = orderTransactionService.checkout(orderCheckoutRequest);
        return ResponseEntity.ok(checkoutResponse);
    }
}
