package co.ohmygoods.shop.service;

import co.ohmygoods.product.repository.ProductCustomCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.model.entity.ProductCustomCategory;
import co.ohmygoods.shop.exception.ShopException;
import co.ohmygoods.shop.repository.ShopRepository;
import co.ohmygoods.shop.service.dto.ShopOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductCustomCategoryRepository productCustomCategoryRepository;

    public ShopOverviewResponse getShopOverview(Long shopId) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(ShopException::notFoundShop);
        var productCategoriesMap = productCustomCategoryRepository.findAllByShop(shop).stream()
                .collect(Collectors.toMap(ProductCustomCategory::getId, ProductCustomCategory::getCustomCategoryName));
        var products = productRepository.findAll(Pageable.ofSize(20));

        return ShopOverviewResponse.builder()
                .name(shop.getName())
                .introduction(shop.getIntroduction())
                .createdAt(shop.getCreatedAt())
                .shopImage(shop.getShopImageName().concat(shop.getIntroduction()))
                .status(shop.getStatus())
                .shopAllProductCategories(productCategoriesMap)
                .products(products.getContent())
                .build();
    }
}
