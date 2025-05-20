package co.ohmygoods.shop.service;


import co.ohmygoods.product.repository.ProductCustomCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductCustomCategory;
import co.ohmygoods.shop.exception.ShopException;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.repository.ShopRepository;
import co.ohmygoods.shop.service.dto.ShopOverviewResponse;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductCustomCategoryRepository productCustomCategoryRepository;

    public ShopOverviewResponse getShopOverview(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(ShopException::notFoundShop);
        Map<Long, String> productCategoriesMap = productCustomCategoryRepository.findAllByShop(shop).stream()
                .collect(Collectors.toMap(ProductCustomCategory::getId, ProductCustomCategory::getCustomCategoryName));
        Page<Product> products = productRepository.findAll(Pageable.ofSize(20));

        return ShopOverviewResponse.of(shop, productCategoriesMap, products.getContent());
    }
}
