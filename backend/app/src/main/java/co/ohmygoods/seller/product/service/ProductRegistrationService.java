package co.ohmygoods.seller.product.service;

import co.ohmygoods.auth.account.exception.AccountNotFoundException;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.product.model.entity.*;
import co.ohmygoods.product.service.dto.ProductCustomCategoryResponse;
import co.ohmygoods.product.service.dto.ProductSeriesResponse;
import co.ohmygoods.product.exception.InvalidProductCustomCategoryException;
import co.ohmygoods.product.exception.InvalidProductSeriesException;
import co.ohmygoods.product.repository.ProductCustomCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.repository.ProductSeriesRepository;
import co.ohmygoods.seller.product.service.dto.ProductBusinessInfo;
import co.ohmygoods.seller.product.service.dto.ProductMetadataModifyInfo;
import co.ohmygoods.seller.product.service.dto.ProductRegisterRequest;
import co.ohmygoods.product.exception.ProductNotFoundException;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.shop.repository.ShopRepository;
import co.ohmygoods.shop.exception.ShopNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductRegistrationService {

    private final AccountRepository accountRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductCustomCategoryRepository productCustomCategoryRepository;
    private final ProductSeriesRepository productSeriesRepository;

    public List<ProductBusinessInfo> getRegisteredProducts(Long shopId) {
        return getRegisteredProducts(shopId, Pageable.ofSize(20));
    }

    public List<ProductBusinessInfo> getRegisteredProducts(Long shopId, Pageable pageable) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var productPage = productRepository.findAllByShop(shop, pageable);

        return productPage.map(product -> {
            var series = product.getSeriesMappings()
                    .stream()
                    .map(seriesMapping -> seriesMapping.getSeries().getSeriesName())
                    .toList();
            var detailCategories = product.getDetailCategoryMappings()
                    .stream()
                    .map(detailCategoryMapping -> detailCategoryMapping.getCustomCategory().getCustomCategoryName())
                    .toList();

            return ProductBusinessInfo.builder()
                    .shopId(shopId)
                    .productId(product.getId())
                    .description(product.getDescription())
                    .category(product.getMainCategory())
                    .stockStatus(product.getStockStatus())
                    .series(series)
                    .customCategories(detailCategories)
                    .quantity(product.getRemainingQuantity())
                    .purchaseLimit(product.getPurchaseMaximumQuantity())
                    .price(product.getOriginalPrice())
                    .discountRate(product.getDiscountRate())
                    .discountEndDate(product.getDiscountStartDate())
                    .discountEndDate(product.getDiscountEndDate())
                    .build();
        }).toList();
    }

    public ProductCustomCategoryResponse registerProductCustomCategory(Long shopId, String email, String customCategoryName) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        var account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException(email));

        shop.ownerCheck(account);

        var optionalProductDetailCategory = productCustomCategoryRepository.findByCategoryName(customCategoryName);

        if (optionalProductDetailCategory.isPresent()) {
            throw InvalidProductCustomCategoryException.duplicateName(customCategoryName);
        }

        var productCustomCategory = ProductCustomCategory.toEntity(shop, customCategoryName);
        var saved = productCustomCategoryRepository.save(productCustomCategory);

        return new ProductCustomCategoryResponse(saved.getId(), saved.getCustomCategoryName());
    }

    public ProductSeriesResponse registerProductSeries(Long shopId, String email, String seriesName) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        var account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException(email));

        shop.ownerCheck(account);

        var optionalProductSeries = productSeriesRepository.findBySeriesName(seriesName);

        if (optionalProductSeries.isPresent()) {
            throw InvalidProductSeriesException.duplicateName(seriesName);
        }

        var productSeries = ProductSeries.toEntity(shop, seriesName);
        var saved = productSeriesRepository.save(productSeries);

        return new ProductSeriesResponse(saved.getId(), saved.getSeriesName());
    }

    public Long registerProduct(ProductRegisterRequest info) {
        var shopId = info.shopId();
        var accountEmail = info.accountEmail();

        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var account = accountRepository.findByEmail(accountEmail)
                .orElseThrow(() -> new AccountNotFoundException(accountEmail));

        shop.ownerCheck(account);

        var product = Product
                .builder()
                .shop(shop)
                .name(info.name())
                .type(info.type())
                .mainCategory(info.category())
                .stockStatus(info.status())
                .originalPrice(info.price())
                .remainingQuantity(Math.max(info.quantity(), 0))
                .purchaseMaximumQuantity(Math.max(info.purchaseLimitCount(), 1))
                .description(info.description())
                .saleStartDate(info.status().equals(ProductStockStatus.TO_BE_SOLD) ? info.expectedSaleDate() : LocalDateTime.now())
                .discountRate(Math.max(info.discountRate(), 0))
                .discountStartDate(info.discountStartDate())
                .discountEndDate(info.discountEndDate())
                .build();

        var savedProduct = productRepository.save(product);

        var customCategoryIds = info.customCategoryIds();
        var seriesIds = info.seriesIds();

        if (customCategoryIds != null && !customCategoryIds.isEmpty()) {
            var productCustomCategories = productCustomCategoryRepository.findAllByIdAndShop(customCategoryIds, shop);
            var productCustomCategoryMappings = productCustomCategories
                    .stream()
                    .map(customCategory -> ProductCustomCategoryMapping.toEntity(savedProduct, customCategory))
                    .toList();

            savedProduct.setDetailCategoryMappings(productCustomCategoryMappings);
        }

        if (seriesIds != null && !seriesIds.isEmpty()) {
            var productSeries = productSeriesRepository.findAllByIdAndShop(seriesIds, shop);
            var productSeriesMappings = productSeries.stream()
                    .map(series -> ProductSeriesMapping.toEntity(savedProduct, series))
                    .toList();

            savedProduct.setSeriesMappings(productSeriesMappings);
        }

        return savedProduct.getId();
    }

    public void modifyProductMetadata(ProductMetadataModifyInfo info) {
        var shopId = info.shopId();
        var accountEmail = info.accountEmail();
        var productId = info.modifyProductId();

        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var account = accountRepository.findByEmail(accountEmail)
                .orElseThrow(() -> new AccountNotFoundException(accountEmail));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        shop.ownerCheck(account);
        product.shopCheck(shop);

        var customCategoryIds = info.modifyCustomCategoryIds();
        var seriesIds = info.modifySeriesIds();
        List<ProductCustomCategoryMapping> modifyProductCustomCategoryMappings = null;
        List<ProductSeriesMapping> modifyProductSeriesMappings = null;

        if (customCategoryIds != null && !customCategoryIds.isEmpty()) {
            var productCustomCategories = (List<ProductCustomCategory>) productCustomCategoryRepository.findAllById(customCategoryIds);
            modifyProductCustomCategoryMappings = productCustomCategories
                    .stream()
                    .map(customCategory -> ProductCustomCategoryMapping.toEntity(product, customCategory))
                    .toList();
        }

        if (seriesIds != null && !seriesIds.isEmpty()) {
            var productSeries = (List<ProductSeries>)productSeriesRepository.findAllById(seriesIds);
            modifyProductSeriesMappings = productSeries.stream()
                    .map(series -> ProductSeriesMapping.toEntity(product, series))
                    .toList();
        }

        product.updateMetadata(
                info.modifyName(),
                info.modifyDescription(),
                info.modifyType(),
                info.modifyTopCategory(),
                modifyProductCustomCategoryMappings,
                modifyProductSeriesMappings);
    }

    public void delete(Long shopId, String accountEmail, Long productId) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var account = accountRepository.findByEmail(accountEmail)
                .orElseThrow(() -> new AccountNotFoundException(accountEmail));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        shop.ownerCheck(account);
        product.shopCheck(shop);

        productRepository.delete(product);
    }
}
