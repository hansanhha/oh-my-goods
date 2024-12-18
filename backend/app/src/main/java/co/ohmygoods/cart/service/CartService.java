package co.ohmygoods.cart.service;

import co.ohmygoods.auth.account.exception.AccountNotFoundException;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.cart.service.dto.AddCartRequest;
import co.ohmygoods.cart.service.dto.UpdateCartQuantityRequest;
import co.ohmygoods.cart.model.entity.Cart;
import co.ohmygoods.cart.exception.CartException;
import co.ohmygoods.cart.repository.CartRepository;
import co.ohmygoods.product.exception.ProductNotFoundException;
import co.ohmygoods.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private static final int CART_MAXIMUM_COUNT = 100;

    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public Page<Cart> getCarts(String email) {
        return getCarts(email, Pageable.ofSize(20));
    }

    public Page<Cart> getCarts(String email, Pageable pageable) {
        var account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException(email));

        return cartRepository.findAllByAccount(account, pageable);
    }

    public void add(AddCartRequest request) {
        var product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId().toString()));

        var account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new AccountNotFoundException(request.email()));

        product.validateSaleStatus();

        var cartCount = cartRepository.countAllByAccount(account);

        if (cartCount > CART_MAXIMUM_COUNT) {
            throw CartException.exceedCartMaximumQuantity();
        }

        var cart = Cart.toEntity(product, account);
        cartRepository.save(cart);
    }

    public void updateQuantity(UpdateCartQuantityRequest request) {
        var cart = cartRepository.findById(request.cartId())
                .orElseThrow(() -> CartException.notFound(request.cartId()));

        var product = cart.getProduct();

        if (product.isValidRequestQuantity(request.updateQuantity())) {
            throw CartException.exceedProductMaximumQuantity(product.getId(), product.getPurchaseMaximumQuantity());
        }

        cart.updateQuantity(request.updateQuantity());
    }

    public void delete(String email, Long cartId) {
        var cart = cartRepository.findById(cartId)
                .orElseThrow(() -> CartException.notFound(cartId));

        cartRepository.delete(cart);
    }
}
