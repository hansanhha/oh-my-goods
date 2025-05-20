package co.ohmygoods.order.controller;

import co.ohmygoods.auth.account.model.vo.AuthenticatedAccount;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import co.ohmygoods.global.swagger.IdempotencyOpenAPI;
import co.ohmygoods.global.swagger.PaginationOpenAPI;
import co.ohmygoods.order.controller.dto.OrderCheckoutWebRequest;
import co.ohmygoods.order.service.OrderManagementService;
import co.ohmygoods.order.service.OrderTransactionService;
import co.ohmygoods.order.service.dto.OrderCheckoutRequest;
import co.ohmygoods.order.service.dto.OrderCheckoutResponse;
import co.ohmygoods.order.service.dto.OrderItemDetailResponse;
import co.ohmygoods.order.service.dto.OrderItemResponse;
import co.ohmygoods.payment.model.vo.PaymentAPIProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;

@Tag(name = "주문", description = "주문 관련 api")
@RequestMapping("/api/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderManagementService orderManagementService;
    private final OrderTransactionService orderTransactionService;

    @Operation(summary = "사용자 주문 내역 조회", description = "사용자의 주문 내역을 최신순으로 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 주문 내역 반환"),
    })
    @GetMapping
    public ResponseEntity<?> getOrders(@AuthenticationPrincipal AuthenticatedAccount account,
                                       @PaginationOpenAPI.PageDescription @RequestParam(required = false, defaultValue = "0") int page,
                                       @PaginationOpenAPI.SizeDescription @RequestParam(required = false, defaultValue = "20") int size) {
        Slice<OrderItemResponse> orders = orderManagementService.getOrders(account.memberId(), Pageable.ofSize(size).withPage(page));
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "사용자 주문 상세 조회", description = "사용자의 특정 주문에 대한 상세 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 주문 상세 반환"),
    })
    @GetMapping("/{orderItemId}")
    public ResponseEntity<?> getOrder(@Parameter(in = ParameterIn.PATH, name = "조회할 주문 아이디") @PathVariable Long orderItemId) {
        OrderItemDetailResponse order = orderManagementService.getOrder(orderItemId);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "사용자 주문 시작", description = IdempotencyOpenAPI.message)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 준비 성공, 결제를 진행할 링크를 반환합니다. 사용자에게 결체를 요청하기 위해 해당 링크로 리다이렉트해야 합니다"),
    })
    @PostMapping
    @Idempotent
    public ResponseEntity<?> checkout(@AuthenticationPrincipal AuthenticatedAccount account,
                                      @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                                      @RequestBody @Validated OrderCheckoutWebRequest request) {

        List<OrderCheckoutRequest.OrderProductDetail> orderProductDetails = request.orderDetails().stream()
                .map(detail -> new OrderCheckoutRequest.OrderProductDetail(
                        detail.productId(), detail.PurchaseQuantity(),
                        detail.isAppliedCoupon() != null ? detail.isAppliedCoupon() : false,
                        detail.appliedCouponId() != null ? detail.appliedCouponId() : -1))
                .toList();

        OrderCheckoutRequest orderCheckoutRequest = new OrderCheckoutRequest(account.memberId(), orderProductDetails,
                PaymentAPIProvider.valueOf(request.orderPaymentMethod().name().toUpperCase()), request.deliveryAddressId(), request.totalOrderPrice());

        OrderCheckoutResponse checkoutResponse = orderTransactionService.checkout(orderCheckoutRequest);
        return ResponseEntity.ok(checkoutResponse);
    }
}
