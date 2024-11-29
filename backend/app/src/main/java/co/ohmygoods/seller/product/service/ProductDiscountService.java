package co.ohmygoods.seller.product.service;

import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.repository.ProductSeriesRepository;
import co.ohmygoods.product.exception.ProductNotFoundException;
import co.ohmygoods.product.exception.ProductSeriesNotFoundException;
import co.ohmygoods.shop.repository.ShopRepository;
import co.ohmygoods.shop.exception.ShopNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductDiscountService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductSeriesRepository productSeriesRepository;

    public void discountOne(Long shopId, Long productId, int discountRate, LocalDateTime discountEndDate) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        product.shopCheck(shop);
        product.discount(Math.max(discountRate, 0), discountEndDate);
    }

    public void discountSeries(Long shopId, Long productId, Long seriesId, int discountRate, LocalDateTime discountEndDate) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var productSeries = productSeriesRepository.findById(seriesId)
                .orElseThrow(() -> new ProductSeriesNotFoundException());

        var products = productRepository.findAllByShopAndSeries(shop, productSeries);
        products.forEach(product -> {
            product.shopCheck(shop);
            product.discount(discountRate, discountEndDate);
        });
    }

    public void discountAll(Long shopId, int discountRate, LocalDateTime discountEndDate) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var products = productRepository.findAll();

        products.forEach(product -> {
            product.shopCheck(shop);
            product.discount(discountRate, discountEndDate);
        });
    }
}
