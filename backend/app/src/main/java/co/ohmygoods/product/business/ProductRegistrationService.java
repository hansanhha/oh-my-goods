package co.ohmygoods.product.business;

import co.ohmygoods.auth.account.exception.AccountNotFoundException;
import co.ohmygoods.auth.account.persistence.AccountRepository;
import co.ohmygoods.product.ProductDetailCategoryRepository;
import co.ohmygoods.product.ProductRepository;
import co.ohmygoods.product.ProductSeriesRepository;
import co.ohmygoods.product.business.dto.ProductBusinessInfo;
import co.ohmygoods.product.business.dto.ProductMetadataModifyInfo;
import co.ohmygoods.product.business.dto.ProductRegisterRequest;
import co.ohmygoods.product.entity.*;
import co.ohmygoods.product.exception.ProductNotFoundException;
import co.ohmygoods.shop.ShopRepository;
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
    private final ProductDetailCategoryRepository productDetailCategoryRepository;
    private final ProductSeriesRepository productSeriesRepository;

    public List<ProductBusinessInfo> getSimpleList(Long shopId) {
        return getSimpleList(shopId, Pageable.ofSize(20));
    }

    public List<ProductBusinessInfo> getSimpleList(Long shopId, Pageable pageable) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var productPage = productRepository.findAllByShop(shop, pageable);

        return productPage.map(product -> {
            var series = product.getProductSeriesMappings()
                    .stream()
                    .map(seriesMapping -> seriesMapping.getProductSeries().getSeriesName())
                    .toList();
            var detailCategories = product.getProductDetailCategoryMappings()
                    .stream()
                    .map(detailCategoryMapping -> detailCategoryMapping.getProductDetailCategory().getDetailCategory())
                    .toList();

            return ProductBusinessInfo.builder()
                    .shopId(shopId)
                    .productId(product.getId())
                    .description(product.getDescription())
                    .category(product.getCategory())
                    .stockStatus(product.getStockStatus())
                    .series(series)
                    .detailCategory(detailCategories)
                    .quantity(product.getRemainingQuantity())
                    .purchaseLimit(product.getPurchaseMaximumQuantity())
                    .price(product.getOriginalPrice())
                    .discountRate(product.getDiscountRate())
                    .discountEndDate(product.getDiscountStartDate())
                    .discountEndDate(product.getDiscountEndDate())
                    .build();
        }).toList();
    }

    public Long register(ProductRegisterRequest info) {
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
                .category(info.category())
                .stockStatus(info.status())
                .originalPrice(info.price())
                .remainingQuantity(Math.max(info.quantity(), 0))
                .purchaseMaximumQuantity(Math.max(info.purchaseLimitCount(), 1))
                .description(info.description())
                .saleStartDate(info.isImmediateSale() ? LocalDateTime.now() : info.expectedSaleDate())
                .discountRate(Math.max(info.discountRate(), 0))
                .discountStartDate(info.discountStartDate())
                .discountEndDate(info.discountEndDate())
                .build();

        var savedProduct = productRepository.save(product);

        var detailCategoryIds = info.detailCategoryIds();
        var seriesIds = info.seriesIds();

        if (!detailCategoryIds.isEmpty()) {
            var productDetailCategories = (List<ProductDetailCategory>) productDetailCategoryRepository.findAllById(detailCategoryIds);
            var productDetailCategoryMappings = productDetailCategories
                    .stream()
                    .map(detailCategory -> ProductDetailCategoryMapping.toEntity(savedProduct, detailCategory))
                    .toList();

            savedProduct.setProductDetailCategoryMappings(productDetailCategoryMappings);
        }

        if (!seriesIds.isEmpty()) {
            var productSeries = (List<ProductSeries>)productSeriesRepository.findAllById(seriesIds);
            var productSeriesMappings = productSeries.stream()
                    .map(series -> ProductSeriesMapping.toEntity(savedProduct, series))
                    .toList();

            savedProduct.setProductSeriesMappings(productSeriesMappings);
        }

        return savedProduct.getId();
    }

    public void modifyMetadata(ProductMetadataModifyInfo info) {
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

        var detailCategoryIds = info.modifyDetailCategoryIds();
        var seriesIds = info.modifySeriesIds();
        List<ProductDetailCategoryMapping> modifyProductDetailCategoryMappings = null;
        List<ProductSeriesMapping> modifyProductSeriesMappings = null;

        if (!detailCategoryIds.isEmpty()) {
            var productDetailCategories = (List<ProductDetailCategory>) productDetailCategoryRepository.findAllById(detailCategoryIds);
            modifyProductDetailCategoryMappings = productDetailCategories
                    .stream()
                    .map(detailCategory -> ProductDetailCategoryMapping.toEntity(product, detailCategory))
                    .toList();
        }

        if (!seriesIds.isEmpty()) {
            var productSeries = (List<ProductSeries>)productSeriesRepository.findAllById(seriesIds);
            modifyProductSeriesMappings = productSeries.stream()
                    .map(series -> ProductSeriesMapping.toEntity(product, series))
                    .toList();
        }

        product.updateMetadata(
                info.modifyName(),
                info.modifyDescription(),
                info.modifyType(),
                info.modifyCategory(),
                modifyProductDetailCategoryMappings,
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