package co.ohmygoods.product.service.admin;


import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.model.entity.CustomCategory;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductCustomCategory;
import co.ohmygoods.product.model.entity.ProductGeneralCategory;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.repository.ProductCustomCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.service.admin.dto.*;
import co.ohmygoods.shop.exception.ShopException;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductAdminService {

    private final ProductAssetAdminService productAssetAdminService;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductCustomCategoryRepository productCustomCategoryRepository;

    public Slice<ProductAdminResponse> getProducts(String adminMemberId, ProductSearchCondition condition, Pageable pageable) {
        Shop shop = shopRepository.findByAdminMemberId(adminMemberId).orElseThrow(ShopException::notFoundShop);
        Slice<Product> products = productRepository.searchAllByShop(shop, condition, pageable);

        return products.map(p -> convertToProductAdminResponse(shop, p));
    }

    public CustomCategoryResponse registerCustomCategory(String adminMemberId, String customCategoryName) {
        Shop shop = shopRepository.findByAdminMemberId(adminMemberId).orElseThrow(ShopException::notFoundShop);

        if (productCustomCategoryRepository.existsByShopAndName(shop, customCategoryName)) {
            throw ProductException.DUPLICATE_CUSTOM_CATEGORY_NAME;
        }

        CustomCategory customCategory = productCustomCategoryRepository.save(CustomCategory.create(shop, customCategoryName));

        return new CustomCategoryResponse(customCategory.getId(), customCategory.getName());
    }

    public long registerProduct(RegisterProductRequest request) {
        Shop shop = shopRepository.findByAdminMemberId(request.adminMemberId()).orElseThrow(ShopException::notFoundShop);

        ProductStockStatus stockStatus = request.isImmediatelySale() ? ProductStockStatus.ON_SALES : ProductStockStatus.TO_BE_SOLD;
        LocalDateTime saleStartDate = stockStatus.equals(ProductStockStatus.ON_SALES) ? LocalDateTime.now() : request.expectedSaleDate();

        Product newProduct = Product
                .builder()
                .shop(shop)
                .name(request.name())
                .type(request.type())
                .category(ProductGeneralCategory.of(request.mainCategory(), request.subCategory()))
                .stockStatus(stockStatus)
                .originalPrice(Math.max(request.price(), 0))
                .remainingQuantity(Math.max(request.quantity(), 0))
                .purchaseMaximumQuantity(Math.max(request.purchaseLimitCount(), 1))
                .description(request.description())
                .saleStartDate(saleStartDate)
                .discountRate(Math.max(request.discountRate(), 0))
                .discountStartDate(request.discountStartDate())
                .discountEndDate(request.discountEndDate())
                .build();

        Product product = productRepository.save(newProduct);

        // 커스텀 카테고리를 지정한 경우
        if (request.customCategoryIds() != null && !request.customCategoryIds().isEmpty()) {
            List<CustomCategory> customCategories = productCustomCategoryRepository.findAllByIdAndShop(request.customCategoryIds(), shop);
            List<ProductCustomCategory> customCategoryMappings = customCategories
                    .stream()
                    .map(customCategory -> ProductCustomCategory.create(product, customCategory))
                    .toList();

            product.addCustomCategories(customCategoryMappings);
        }

        // 상품 이미지, 동영상 업로드
        if (request.assets() != null && !request.assets().isEmpty()) {
            productAssetAdminService.upload(product, request.adminMemberId(), request.assets());
        }

        return product.getId();
    }

    public ProductAdminResponse updateProduct(UpdateProductMetadataRequest request) {
        var shop = shopRepository.findByAdminMemberId(request.ownerMemberId()).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(request.updateProductId()).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);

        if (request.updateMainCategory().notContains(request.updateSubCategory())) {
            throw ProductException.INVALID_SUB_CATEGORY;
        }

        var customCategoryIds = request.updateCustomCategoryIds();
        List<ProductCustomCategory> updateProductCustomCategories = null;

        if (customCategoryIds != null && !customCategoryIds.isEmpty()) {
            var productCustomCategories = (List<CustomCategory>) productCustomCategoryRepository.findAllById(customCategoryIds);
            updateProductCustomCategories = productCustomCategories
                    .stream()
                    .map(customCategory -> ProductCustomCategory.create(product, customCategory))
                    .toList();
        }

        product.updateMetadata(
                request.updateName(),
                request.updateDescription(),
                request.updateType(),
                ProductGeneralCategory.of(request.updateMainCategory(), request.updateSubCategory()),
                updateProductCustomCategories);

        if (request.updateAssets() != null && !request.updateAssets().isEmpty()) {
            productAssetAdminService.replace(product, request.ownerMemberId(), request.updateAssets());
        }

        return convertToProductAdminResponse(shop, product);
    }

    public void deleteProduct(Long productId) {
        var product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        productAssetAdminService.delete(product);

        productRepository.delete(product);
    }

    private ProductAdminResponse convertToProductAdminResponse(Shop shop, Product product) {
        List<CustomCategoryResponse> customCategories = product.getCustomCategories()
                .stream()
                .map(ProductCustomCategory::getCustomCategory)
                .map(CustomCategoryResponse::from)
                .toList();

        return new ProductAdminResponse(
                shop.getId(),
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getType(),
                product.getCategory().getMainCategory(),
                product.getCategory().getSubCategory(),
                product.getStockStatus(),
                customCategories,
                product.getRemainingQuantity(),
                product.getPurchaseMaximumQuantity(),
                product.getOriginalPrice(),
                product.getDiscountRate(),
                product.getDiscountStartDate(),
                product.getDiscountEndDate(),
                product.getCreatedAt(),
                product.getLastModifiedAt());
    }
}
