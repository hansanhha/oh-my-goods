package co.ohmygoods.cart.service;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.cart.service.dto.AddCartRequest;
import co.ohmygoods.cart.service.dto.CartResponse;
import co.ohmygoods.cart.service.dto.UpdateCartQuantityRequest;
import co.ohmygoods.cart.model.entity.Cart;
import co.ohmygoods.cart.exception.CartException;
import co.ohmygoods.cart.repository.CartRepository;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.shop.model.entity.Shop;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    public Slice<CartResponse> getAll(String memberId, Pageable pageable) {
        Slice<Cart> carts = cartRepository.fetchAllShopAndProductByMemberId(memberId, pageable);

        return carts.map(c -> {
            Product p = c.getProduct();
            Shop s = p.getShop();

            int discountedPrice = c.getOriginalPriceWhenPutIn();

            if (p.isDiscounted()) {
                discountedPrice = p.calculateActualPrice();
            }

            return new CartResponse(s.getId(), s.getName(), p.getId(), p.getName(), c.getId(),
                    c.getOriginalPriceWhenPutIn(), discountedPrice, c.getQuantity(), p.getOrderableQuantity());
        });
    }

    public void add(AddCartRequest request) {
        Product product = productRepository.findById(request.productId()).orElseThrow(CartException::notFoundCart);
        Account account = accountRepository.findByMemberId(request.memberId()).orElseThrow(CartException::notFoundCart);

        product.validateOnSaleStatus();

        int totalKeepCartCount = cartRepository.countAllByAccount(account);

        if (totalKeepCartCount >= CART_MAXIMUM_COUNT) {
            throw CartException.EXCEED_CART_MAX_LIMIT;
        }

        cartRepository.save(Cart.create(product, account));
    }

    public void updateQuantity(UpdateCartQuantityRequest request) {
        Cart cart = cartRepository.findById(request.cartId()).orElseThrow(CartException::notFoundCart);
        Product product = cart.getProduct();

        if (product.isValidPurchaseQuantity(request.updateQuantity())) {
            throw CartException.EXCEED_PRODUCT_MAX_LIMIT;
        }

        cart.updateQuantity(request.updateQuantity());
    }

    public void delete(Long cartId) {
        var cart = cartRepository.findById(cartId).orElseThrow(CartException::notFoundCart);

        cartRepository.delete(cart);
    }
}
