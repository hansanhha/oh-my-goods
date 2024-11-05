package co.ohmygoods.sale.product.admin;

import co.ohmygoods.auth.account.AccountRepository;
import co.ohmygoods.domain.account.exception.AccountNotFoundException;
import co.ohmygoods.domain.product.entity.*;
import co.ohmygoods.sale.product.ProductDetailCategoryRepository;
import co.ohmygoods.sale.product.ProductRepository;
import co.ohmygoods.sale.product.ProductSeriesRepository;
import co.ohmygoods.sale.product.dto.ProductMetadataModifyInfo;
import co.ohmygoods.sale.product.dto.ProductRegisterInfo;
import co.ohmygoods.domain.product.exception.ProductNotFoundException;
import co.ohmygoods.sale.shop.ShopRepository;
import co.ohmygoods.domain.shop.exception.ShopNotFoundException;
import lombok.RequiredArgsConstructor;
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

    public Long register(ProductRegisterInfo info) {
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
                .status(info.status())
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
