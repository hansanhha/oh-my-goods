package co.ohmygoods.cart.controller;

import co.ohmygoods.auth.account.model.vo.AuthenticatedAccount;
import co.ohmygoods.cart.controller.dto.AddCartWebRequest;
import co.ohmygoods.cart.model.entity.Cart;
import co.ohmygoods.cart.service.CartService;
import co.ohmygoods.cart.service.dto.AddCartRequest;
import co.ohmygoods.cart.service.dto.UpdateCartQuantityRequest;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import co.ohmygoods.global.swagger.IdempotencyOpenAPI;
import co.ohmygoods.global.swagger.PaginationOpenAPI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;

@Tag(name = "장바구니", description = "장바구니 관련 api")
@RequestMapping("/api/carts")
@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 조회", description = "장바구니에 담긴 모든 상품을 조회합니다")
    @ApiResponse(responseCode = "200", description = "장바구니에 담긴 상품 리스트 반환",
            content = @Content(schema = @Schema(implementation = CartWebResponse.class)))
    @GetMapping
    public ResponseEntity<?> getCart(@AuthenticationPrincipal AuthenticatedAccount account,
                                     @PaginationOpenAPI.PageDescription @RequestParam(required = false, defaultValue = "0") int page,
                                     @PaginationOpenAPI.SizeDescription @RequestParam(required = false, defaultValue = "20") int size) {
        Page<Cart> carts = cartService.get(account.memberId(), Pageable.ofSize(size).withPage(page));
        return ResponseEntity.ok(carts);
    }

    @Operation(summary = "장바구니에 상품 담기", description = IdempotencyOpenAPI.message)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니에 상품 담김"),
    })
    @PostMapping
    @Idempotent
    public void addCart(@AuthenticationPrincipal AuthenticatedAccount account,
                        @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                        @RequestBody @Validated AddCartWebRequest request) {
        cartService.add(new AddCartRequest(account.memberId(), request.productId()));
    }

    @Operation(summary = "장바구니에 담긴 상품 수량 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니 상품 수량 변경됨"),
    })
    @PutMapping("/{cartId}")
    public void updateCartQuantity(@PathVariable Long cartId,
                                   @RequestParam(name = "quantity") int quantity) {
        cartService.updateQuantity(new UpdateCartQuantityRequest(cartId, quantity));
    }

    @Operation(summary = "장바구니에 담긴 상품 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니 상품 삭제됨"),
    })
    @DeleteMapping("/{cartId}")
    public void deleteCart(@PathVariable Long cartId) {
        cartService.delete(cartId);
    }

    public record CartWebResponse() {

    }
}
