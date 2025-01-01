package co.ohmygoods.seller.product.service;

import co.ohmygoods.auth.account.exception.AccountNotFoundException;
import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.product.exception.InvalidProductCustomCategoryException;
import co.ohmygoods.product.exception.ProductNotFoundException;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductCustomCategory;
import co.ohmygoods.product.model.entity.ProductCustomCategoryMapping;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.repository.ProductCustomCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.seller.product.service.dto.CustomCategoryResponse;
import co.ohmygoods.seller.product.service.dto.RegisterProductRequest;
import co.ohmygoods.seller.product.service.dto.SellerProductResponse;
import co.ohmygoods.seller.product.service.dto.UpdateProductMetadataRequest;
import co.ohmygoods.shop.exception.ShopNotFoundException;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SellerProductRegistrationService {

    private final AccountRepository accountRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductCustomCategoryRepository productCustomCategoryRepository;

    public List<SellerProductResponse> getRegisteredProducts(String ownerMemberId, Pageable pageable) {
        Shop shop = shopRepository.findByOwnerMemberId(ownerMemberId).orElseThrow(() -> new ShopNotFoundException(""));
        Slice<Product> products = productRepository.findAllByShop(shop, pageable);

        return products.map(this::convertSellerProductResponse).toList();
    }

    public CustomCategoryResponse registerCustomCategory(String ownerMemberId, String email, String customCategoryName) {
        Shop shop = shopRepository.findByOwnerMemberId(ownerMemberId).orElseThrow(() -> new ShopNotFoundException(""));
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new AccountNotFoundException(email));

        if (productCustomCategoryRepository.existsByCustomCategoryName(customCategoryName)) {
            throw InvalidProductCustomCategoryException.duplicateName(customCategoryName);
        }

        ProductCustomCategory productCustomCategory = ProductCustomCategory.toEntity(shop, customCategoryName);
        ProductCustomCategory saved = productCustomCategoryRepository.save(productCustomCategory);

        return new CustomCategoryResponse(saved.getId(), saved.getCustomCategoryName());
    }

    public SellerProductResponse registerProduct(RegisterProductRequest request) {
        var shop = shopRepository.findByOwnerMemberId(request.ownerMemberId())
                .orElseThrow(() -> new ShopNotFoundException(""));

        var product = Product
                .builder()
                .shop(shop)
                .name(request.name())
                .type(request.type())
                .mainCategory(request.mainCategory())
                .subCategory(request.subCategory())
                .stockStatus(request.status())
                .originalPrice(request.price())
                .remainingQuantity(Math.max(request.quantity(), 0))
                .purchaseMaximumQuantity(Math.max(request.purchaseLimitCount(), 1))
                .description(request.description())
                .saleStartDate(request.status().equals(ProductStockStatus.TO_BE_SOLD) ? request.expectedSaleDate() : LocalDateTime.now())
                .discountRate(Math.max(request.discountRate(), 0))
                .discountStartDate(request.discountStartDate())
                .discountEndDate(request.discountEndDate())
                .build();

        var savedProduct = productRepository.save(product);

        // 커스텀 카테고리를 지정한 경우
        if (request.customCategoryIds() != null && !request.customCategoryIds().isEmpty()) {
            var customCategories = productCustomCategoryRepository.findAllByIdAndShop(request.customCategoryIds(), shop);
            var customCategoryMappings = customCategories
                    .stream()
                    .map(customCategory -> ProductCustomCategoryMapping.create(savedProduct, customCategory))
                    .toList();

            savedProduct.addCustomCategories(customCategoryMappings);
        }

        return convertSellerProductResponse(product);
    }

    public SellerProductResponse updateProductMetadata(UpdateProductMetadataRequest request) {
        var shop = shopRepository.findByOwnerMemberId(request.ownerMemberId())
                .orElseThrow(() -> new ShopNotFoundException(""));
        var product = productRepository.findById(request.modifyProductId())
                .orElseThrow(() -> new ProductNotFoundException(""));

        product.shopCheck(shop);

        var customCategoryIds = request.modifyCustomCategoryIds();
        List<ProductCustomCategoryMapping> modifyProductCustomCategoryMappings = null;

        if (customCategoryIds != null && !customCategoryIds.isEmpty()) {
            var productCustomCategories = (List<ProductCustomCategory>) productCustomCategoryRepository.findAllById(customCategoryIds);
            modifyProductCustomCategoryMappings = productCustomCategories
                    .stream()
                    .map(customCategory -> ProductCustomCategoryMapping.create(product, customCategory))
                    .toList();
        }

        product.updateMetadata(
                request.modifyName(),
                request.modifyDescription(),
                request.modifyType(),
                request.modifyTopCategory(),
                modifyProductCustomCategoryMappings);

        return convertSellerProductResponse(product);
    }

    public void delete(String ownerMemberId, Long productId) {
        var shop = shopRepository.findByOwnerMemberId(ownerMemberId)
                .orElseThrow(() -> new ShopNotFoundException(""));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(""));

        product.shopCheck(shop);

        productRepository.delete(product);
    }

    private SellerProductResponse convertSellerProductResponse(Product product) {
        var customCategoryResponses = product.getCustomCategoriesMappings()
                .stream()
                .map(ProductCustomCategoryMapping::getCustomCategory)
                .map(CustomCategoryResponse::from)
                .toList();

        return SellerProductResponse.builder()
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
                .productLastModifiedAt(product.getLastModifiedAt())
                .build();
    }
}
