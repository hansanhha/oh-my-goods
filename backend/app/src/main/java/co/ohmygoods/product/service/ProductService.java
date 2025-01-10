package co.ohmygoods.product.service;

import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductCustomCategory;
import co.ohmygoods.product.model.entity.ProductCustomCategoryMapping;
import co.ohmygoods.product.model.entity.ProductGeneralCategory;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.repository.ProductCustomCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.repository.dto.ProductShopDto;
import co.ohmygoods.product.service.dto.ProductCustomCategoryResponse;
import co.ohmygoods.product.service.dto.ProductResponse;
import co.ohmygoods.shop.exception.ShopException;
import co.ohmygoods.shop.model.entity.Shop;
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

    public List<ProductResponse> getProductsByCategory(ProductMainCategory mainCategory, ProductSubCategory subCategory, Pageable pageable) {
        Slice<ProductShopDto> productShopDtos = productRepository
                .fetchAllSalesProductByGeneralCategory(ProductGeneralCategory.of(mainCategory, subCategory), pageable);

        return productShopDtos
                .map(dto -> convertProductResponse(dto.shopId(), dto.shopName(), dto.product()))
                .toList();
    }

    public List<ProductResponse> getProductsByShopAndCategory(Long shopId, ProductMainCategory mainCategory, ProductSubCategory subCategory, Pageable pageable) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(ShopException::notFoundShop);

        Slice<Product> products = productRepository.fetchAllSalesProductByShopAndCategory(shop, ProductGeneralCategory.of(mainCategory, subCategory), pageable);

        return products.map(product -> convertProductResponse(shop.getId(), shop.getName(), product)).toList();
    }

    public List<ProductResponse> getProductsByShopAndCustomCategory(Long shopId, Long customCategoryId, Pageable pageable) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(ShopException::notFoundShop);

        ProductCustomCategory customCategory = customCategoryRepository.findById(customCategoryId)
                .orElseThrow(ProductException::notFoundCategory);

        Slice<Product> products = productRepository.fetchAllSalesProductByShopAndCustomCategory(shop, customCategory, pageable);

        return products.map(product -> convertProductResponse(shop.getId(), shop.getName(), product)).toList();
    }

    private ProductResponse convertProductResponse(Long shopId, String shopName, Product product) {
        List<ProductCustomCategoryResponse> customCategoryResponses = product.getCustomCategories()
                .stream()
                .map(ProductCustomCategoryMapping::getCustomCategory)
                .map(ProductCustomCategoryResponse::from)
                .toList();

        return ProductResponse.builder()
                .shopId(shopId)
                .shopName(shopName)
                .productId(product.getId())
                .productDescription(product.getDescription())
                .productMainCategory(product.getCategory().getMainCategory())
                .productSubCategory(product.getCategory().getSubCategory())
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
