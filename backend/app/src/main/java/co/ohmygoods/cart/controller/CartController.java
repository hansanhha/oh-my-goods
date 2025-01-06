package co.ohmygoods.cart.controller;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.cart.controller.dto.AddCartWebRequest;
import co.ohmygoods.cart.model.entity.Cart;
import co.ohmygoods.cart.service.CartService;
import co.ohmygoods.cart.service.dto.AddCartRequest;
import co.ohmygoods.cart.service.dto.UpdateCartQuantityRequest;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;

@RequestMapping("/api/carts")
@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCart(@AuthenticationPrincipal AuthenticatedAccount account,
                                     @RequestParam(required = false, defaultValue = "0") int page,
                                     @RequestParam(required = false, defaultValue = "20") int size) {
        Page<Cart> carts = cartService.get(account.memberId(), Pageable.ofSize(size).withPage(page));
        return ResponseEntity.ok(carts);
    }

    @PostMapping
    @Idempotent
    public void addCart(@AuthenticationPrincipal AuthenticatedAccount account,
                        @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                        @RequestBody @Validated AddCartWebRequest request) {
        cartService.add(new AddCartRequest(account.memberId(), request.productId()));
    }

    @PutMapping("/{cartId}")
    public void updateCartQuantity(@PathVariable Long cartId,
                                   @RequestParam(name = "quantity") int quantity) {
        cartService.updateQuantity(new UpdateCartQuantityRequest(cartId, quantity));
    }

    @DeleteMapping("/{cartId}")
    public void deleteCart(@PathVariable Long cartId) {
        cartService.delete(cartId);
    }
}
