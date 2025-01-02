package co.ohmygoods.product.model.entity;

import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.product.exception.InvalidProductUpdateParameterException;
import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.exception.ProductShopCheckException;
import co.ohmygoods.product.exception.ProductStockStatusException;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.model.vo.ProductType;
import co.ohmygoods.shop.model.entity.Shop;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product extends BaseEntity {

    public static int REMAINING_QUANTITY_MINIMUM = 0;
    public static int PURCHASE_QUANTITY_MINIMUM = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private ProductType type;

    @Enumerated(EnumType.STRING)
    private ProductMainCategory mainCategory;

    @Column(nullable = false)
    private String subCategory;

    @Enumerated(EnumType.STRING)
    private ProductStockStatus stockStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCustomCategoryMapping> customCategoriesMappings = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int remainingQuantity;

    @Column(nullable = false)
    private int purchaseMaximumQuantity;

    @Column(nullable = false)
    private int originalPrice;

    private LocalDateTime saleStartDate;

    private LocalDateTime saleEndDate;

    private int discountRate;

    private LocalDateTime discountStartDate;

    private LocalDateTime discountEndDate;

    public void shopCheck(Shop shop) {
        if (this.shop.getId().equals(shop.getId())) {
            throw new ProductShopCheckException(shop.getId().toString());
        }
    }

    public void addCustomCategories(List<ProductCustomCategoryMapping> customCategoriesMappings) {
        this.customCategoriesMappings.addAll(customCategoriesMappings);
    }

    public void updateMetadata(String name,
                               String description,
                               ProductType type,
                               ProductMainCategory mainCategory,
                               String subCategory,
                               List<ProductCustomCategoryMapping> productCustomCategoryMappings) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.customCategoriesMappings = productCustomCategoryMappings;
    }

    public int calculateActualPrice() {
        double discountPrice = originalPrice - (originalPrice * (double) discountRate / 100);
        BigDecimal halfUpDiscountPrice = BigDecimal.valueOf(discountPrice).setScale(0, RoundingMode.HALF_UP);
        return halfUpDiscountPrice.intValue();
    }

    public void updateRemainingQuantity(int remainingQuantity) {
        if (remainingQuantity < REMAINING_QUANTITY_MINIMUM) {
            throw new InvalidProductUpdateParameterException();
        }

        this.remainingQuantity = remainingQuantity;
    }

    public void updatePurchaseMaximumQuantity(int purchaseMaximumQuantity) {
        if (purchaseMaximumQuantity < PURCHASE_QUANTITY_MINIMUM) {
            throw new InvalidProductUpdateParameterException();
        }

        this.purchaseMaximumQuantity = purchaseMaximumQuantity;
    }

    public void updateStockStatus(ProductStockStatus stockStatus) {
        switch (stockStatus) {
            case ProductStockStatus.SOLDOUT -> {
                if (this.remainingQuantity > REMAINING_QUANTITY_MINIMUM) {
                    throw new InvalidProductUpdateParameterException();
                }
            }
            case ProductStockStatus.TO_BE_RESTOCKED -> {
                if (!this.stockStatus.equals(ProductStockStatus.SOLDOUT)) {
                    throw new InvalidProductUpdateParameterException();
                }
            }
            case ProductStockStatus.RESTOCKED -> {
                if (!this.stockStatus.equals(ProductStockStatus.TO_BE_RESTOCKED)) {
                    throw new InvalidProductUpdateParameterException();
                }
            }
        }
        this.stockStatus = stockStatus;
    }

    public void discount(int discountRate, LocalDateTime discountEndDate) {
        this.discountRate = Math.max(discountRate, 0);
        this.discountEndDate = discountEndDate;
    }

    public boolean isValidRequestQuantity(int quantity) {
        return purchaseMaximumQuantity >= quantity && remainingQuantity >= quantity;
    }

    public void validateSaleStatus() {
        if (stockStatus.equals(ProductStockStatus.ON_SALES))
            ProductStockStatusException.throwInvalidStatus(stockStatus);
    }

    public void decrease(int quantity) {
        validateSaleStatus();

        if (!isValidRequestQuantity(quantity)) {
            ProductException.throwCauseInvalidDecreaseQuantity(purchaseMaximumQuantity, remainingQuantity, quantity);
        }

        remainingQuantity -= quantity;

        if (remainingQuantity < 0) {
            ProductException.throwCauseInvalidDecreaseQuantity(purchaseMaximumQuantity, remainingQuantity, quantity);
        }
    }

    public void increase(int quantity) {
        if (quantity < 0) {
            ProductException.throwCauseInvalidIncreaseQuantity(quantity);
        }

        remainingQuantity += quantity;
    }
}
