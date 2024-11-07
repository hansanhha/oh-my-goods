package co.ohmygoods.shop;

import co.ohmygoods.product.ProductDetailCategoryRepository;
import co.ohmygoods.product.ProductRepository;
import co.ohmygoods.product.ProductSeriesRepository;
import co.ohmygoods.product.entity.Product;
import co.ohmygoods.product.entity.ProductDetailCategory;
import co.ohmygoods.product.entity.ProductSeries;
import co.ohmygoods.shop.dto.ShopOverviewResponse;
import co.ohmygoods.shop.exception.ShopNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductSeriesRepository productSeriesRepository;
    private final ProductDetailCategoryRepository productDetailCategoryRepository;

    public ShopOverviewResponse getShopOverview(Long shopId) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var productSeries = productSeriesRepository.findAllByShop(shop)
                .stream().collect(Collectors.toMap(ProductSeries::getId, ProductSeries::getSeriesName));
        var productCategoriesMap = productDetailCategoryRepository.findAllByShop(shop).stream()
                .collect(Collectors.toMap(ProductDetailCategory::getId, ProductDetailCategory::getDetailCategory));
        var products = productRepository.findAll(Pageable.ofSize(20));

        return ShopOverviewResponse.builder()
                .name(shop.getName())
                .introduction(shop.getIntroduction())
                .createdAt(shop.getCreatedAt())
                .shopImage(shop.getShopImageName().concat(shop.getIntroduction()))
                .status(shop.getStatus())
                .shopAllProductSeries(productSeries)
                .shopAllProductCategories(productCategoriesMap)
                .products(products.getContent())
                .build();
    }
}
