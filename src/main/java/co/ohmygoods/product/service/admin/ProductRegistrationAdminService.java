package co.ohmygoods.product.service.admin;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductGeneralCategory;
import co.ohmygoods.product.model.entity.ProductCustomCategory;
import co.ohmygoods.product.model.entity.ProductCustomCategoryMapping;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.repository.ProductCustomCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.service.admin.dto.CustomCategoryResponse;
import co.ohmygoods.product.service.admin.dto.ProductRegisterRequest;
import co.ohmygoods.product.service.admin.dto.ShopProductResponse;
import co.ohmygoods.product.service.admin.dto.ProductMetadataUpdateRequest;
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
public class ProductRegistrationAdminService {

    private final ProductAssetAdminService productAssetAdminService;
    private final AccountRepository accountRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductCustomCategoryRepository productCustomCategoryRepository;

    public List<ShopProductResponse> getRegisteredProducts(String ownerMemberId, Pageable pageable) {
        Shop shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        Slice<Product> products = productRepository.findAllByShop(shop, pageable);

        return products.map(this::convertSellerProductResponse).toList();
    }

    public CustomCategoryResponse registerCustomCategory(String ownerMemberId, String email, String customCategoryName) {
        Shop shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        Account account = accountRepository.findByEmail(email).orElseThrow(AuthException::notFoundAccount);

        if (productCustomCategoryRepository.existsByCustomCategoryName(customCategoryName)) {
            throw ProductException.DUPLICATE_CUSTOM_CATEGORY_NAME;
        }

        ProductCustomCategory productCustomCategory = ProductCustomCategory.toEntity(shop, customCategoryName);
        ProductCustomCategory saved = productCustomCategoryRepository.save(productCustomCategory);

        return new CustomCategoryResponse(saved.getId(), saved.getCustomCategoryName());
    }

    public ShopProductResponse registerProduct(ProductRegisterRequest request) {
        Shop shop = shopRepository.findByAdminMemberId(request.ownerMemberId()).orElseThrow(ShopException::notFoundShop);

        ProductStockStatus stockStatus = request.isImmediatelySale() ? ProductStockStatus.ON_SALES : ProductStockStatus.TO_BE_SOLD;
        LocalDateTime saleStartDate = stockStatus.equals(ProductStockStatus.ON_SALES) ? LocalDateTime.now() : request.expectedSaleDate();

        if (!request.mainCategory().contains(request.subCategory())) {
            throw ProductException.INVALID_SUB_CATEGORY;
        }

        Product product = Product
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

        Product savedProduct = productRepository.save(product);

        // 커스텀 카테고리를 지정한 경우
        if (request.customCategoryIds() != null && !request.customCategoryIds().isEmpty()) {
            var customCategories = productCustomCategoryRepository.findAllByIdAndShop(request.customCategoryIds(), shop);
            var customCategoryMappings = customCategories
                    .stream()
                    .map(customCategory -> ProductCustomCategoryMapping.create(savedProduct, customCategory))
                    .toList();

            savedProduct.addCustomCategories(customCategoryMappings);
        }

        // 상품 이미지, 동영상 업로드
        if (request.assets() != null && request.assets().length > 0) {
            productAssetAdminService.upload(savedProduct.getId(), request.ownerMemberId(), request.assets());
        }

        return convertSellerProductResponse(product);
    }

    public ShopProductResponse updateProductMetadata(ProductMetadataUpdateRequest request) {
        var shop = shopRepository.findByAdminMemberId(request.ownerMemberId()).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(request.updateProductId()).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);

        if (!request.updateMainCategory().contains(request.updateSubCategory())) {
            throw ProductException.INVALID_SUB_CATEGORY;
        }

        var customCategoryIds = request.updateCustomCategoryIds();
        List<ProductCustomCategoryMapping> updateProductCustomCategoryMappings = null;

        if (customCategoryIds != null && !customCategoryIds.isEmpty()) {
            var productCustomCategories = (List<ProductCustomCategory>) productCustomCategoryRepository.findAllById(customCategoryIds);
            updateProductCustomCategoryMappings = productCustomCategories
                    .stream()
                    .map(customCategory -> ProductCustomCategoryMapping.create(product, customCategory))
                    .toList();
        }

        product.updateMetadata(
                request.updateName(),
                request.updateDescription(),
                request.updateType(),
                ProductGeneralCategory.of(request.updateMainCategory(), request.updateSubCategory()),
                updateProductCustomCategoryMappings);

        if (request.updateAssets() != null && request.updateAssets().length > 0) {
            productAssetAdminService.replace(product.getId(), request.ownerMemberId(), request.updateAssets());
        }

        return convertSellerProductResponse(product);
    }

    public void delete(String ownerMemberId, Long productId) {
        var shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);

        productRepository.delete(product);
    }

    private ShopProductResponse convertSellerProductResponse(Product product) {
        List<CustomCategoryResponse> customCategories = product.getCustomCategories()
                .stream()
                .map(ProductCustomCategoryMapping::getCustomCategory)
                .map(CustomCategoryResponse::from)
                .toList();

        return ShopProductResponse.of(product.getShop().getId(), product, customCategories);
    }
}
