package co.ohmygoods.seller.product.service;

import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.shop.exception.ShopException;
import co.ohmygoods.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class SellerProductDiscountService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    public void discountOne(String ownerMemberId, Long productId, int discountRate, LocalDateTime discountEndDate) {
        var shop = shopRepository.findByOwnerMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);
        product.discount(Math.max(discountRate, 0), discountEndDate);
    }

    public void discountAll(String ownerMemberId, int discountRate, LocalDateTime discountEndDate) {
        var shop = shopRepository.findByOwnerMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        var products = productRepository.findAll();

        products.forEach(product -> {
            product.shopCheck(shop);
            product.discount(discountRate, discountEndDate);
        });
    }
}
