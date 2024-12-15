package co.ohmygoods.product.service;

import co.ohmygoods.product.exception.CustomCategoryNotFoundException;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductCustomCategoryMapping;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.repository.ProductCustomCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.service.dto.ProductCustomCategoryResponse;
import co.ohmygoods.product.service.dto.ProductResponse;
import co.ohmygoods.shop.entity.Shop;
import co.ohmygoods.shop.exception.ShopNotFoundException;
import co.ohmygoods.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductCustomCategoryRepository customCategoryRepository;

    public List<ProductResponse> getProductsByMainCategory(ProductMainCategory productMainCategory, Pageable pageable) {
        Slice<Product> products = productRepository.fetchAllByMainCategoryAndStockStatusOnSales(productMainCategory, pageable);

        return products.stream().map(this::convertProductResponse).toList();
    }

    public List<ProductResponse> getProductsByShopAndMainCategory(Long shopId, ProductMainCategory productMainCategory, Pageable pageable) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        Slice<Product> products = productRepository.fetchAllByShopAndMainCategoryAndStockStatusOnSales(shop, productMainCategory, pageable);

        return products.stream().map(this::convertProductResponse).toList();
    }

    public List<ProductResponse> getProductsByShopAndSubCategory(Long shopId, String productSubCategory, Pageable pageable) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        Slice<Product> products = productRepository.fetchAllByShopAndSubCategoryAndStockStatusOnSales(shop, productSubCategory, pageable);

        return products.stream().map(this::convertProductResponse).toList();
    }

    public List<ProductResponse> getProductsByShopAndCustomCategory(Long shopId, Long detailCategoryId, Pageable pageable) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        var customCategoryId = customCategoryRepository.findById(detailCategoryId)
                .orElseThrow(CustomCategoryNotFoundException::new);

        var products = productRepository.fetchAllByShopAndCustomCategoryAndStockStatusOnSales(shop, customCategoryId, pageable);

        return products.stream().map(this::convertProductResponse).toList();
    }

    private ProductResponse convertProductResponse(Product product) {
        List<ProductCustomCategoryResponse> customCategoryResponses = product.getCustomCategoriesMappings()
                .stream()
                .map(ProductCustomCategoryMapping::getCustomCategory)
                .map(ProductCustomCategoryResponse::from)
                .toList();

        return ProductResponse.builder()
                .shopId(product.getShop().getId())
                .productId(product.getId())
                .productDescription(product.getDescription())
                .productMainCategory(product.getMainCategory())
                .productSubCategory(product.getSubCategory())
                .productStockStatus(product.getStockStatus())
                .productCustomCategories(customCategoryResponses)
                .productQuantity(product.getRemainingQuantity())
                .productPurchaseLimit(product.getPurchaseMaximumQuantity())
                .productPrice(product.getOriginalPrice())
                .productDiscountRate(product.getDiscountRate())
                .productDiscountStartDate(product.getDiscountStartDate())
                .productDiscountEndDate(product.getDiscountEndDate())
                .productRegisteredAt(product.getCreatedAt())
                .build();
    }
}
