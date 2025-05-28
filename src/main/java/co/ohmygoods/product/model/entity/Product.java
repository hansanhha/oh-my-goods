package co.ohmygoods.product.model.entity;

import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.product.exception.ProductException;
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
    private ProductStockStatus stockStatus;

    @Embedded
    private ProductGeneralCategory category;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCustomCategory> customCategories = new ArrayList<>();

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
            throw ProductException.NOT_SALES_STATUS;
        }
    }

    public void addCustomCategories(List<ProductCustomCategory> customCategories) {
        this.customCategories.addAll(customCategories);
    }

    public void updateMetadata(String name,
                               String description,
                               ProductType type,
                               ProductGeneralCategory category,
                               List<ProductCustomCategory> productCustomCategories) {

        this.name = name;
        this.description = description;
        this.type = type;
        this.category = category;
        this.customCategories = productCustomCategories;
    }

    public int getOrderableQuantity() {
        return purchaseMaximumQuantity >= remainingQuantity ? remainingQuantity : purchaseMaximumQuantity;
    }

    public boolean isDiscounted() {
        LocalDateTime now = LocalDateTime.now();

        return discountStartDate != null && discountEndDate != null
                && now.isAfter(discountStartDate) && now.isBefore(discountEndDate) && discountRate > 0;
    }

    public int calculateActualPrice() {
        double discountPrice = originalPrice - (originalPrice * (double) discountRate / 100);
        BigDecimal halfUpDiscountPrice = BigDecimal.valueOf(discountPrice).setScale(0, RoundingMode.HALF_UP);
        return halfUpDiscountPrice.intValue();
    }

    public void updateRemainingQuantity(int remainingQuantity) {
        if (remainingQuantity < REMAINING_QUANTITY_MINIMUM) {
            throw ProductException.INVALID_PRODUCT_QUANTITY;
        }

        this.remainingQuantity = remainingQuantity;
    }

    public void updatePurchaseMaximumQuantity(int purchaseMaximumQuantity) {
        if (purchaseMaximumQuantity < PURCHASE_QUANTITY_MINIMUM) {
            throw ProductException.INVALID_PURCHASE_QUANTITY;
        }

        this.purchaseMaximumQuantity = purchaseMaximumQuantity;
    }

    public void updateStockStatus(ProductStockStatus stockStatus) {
        switch (stockStatus) {
            case ProductStockStatus.SOLDOUT -> {
                if (this.remainingQuantity > REMAINING_QUANTITY_MINIMUM) {
                    throw ProductException.CANNOT_UPDATE_PRODUCT_STATUS;
                }
            }
            case ProductStockStatus.TO_BE_RESTOCKED -> {
                if (!this.stockStatus.equals(ProductStockStatus.SOLDOUT)) {
                    throw ProductException.CANNOT_UPDATE_PRODUCT_STATUS;
                }
            }
            case ProductStockStatus.RESTOCKED -> {
                if (!this.stockStatus.equals(ProductStockStatus.TO_BE_RESTOCKED)) {
                    throw ProductException.CANNOT_UPDATE_PRODUCT_STATUS;
                }
            }
        }
        this.stockStatus = stockStatus;
    }

    public void discount(int discountRate, LocalDateTime discountEndDate) {
        this.discountRate = Math.max(discountRate, 0);
        this.discountEndDate = discountEndDate;
    }

    public boolean isValidPurchaseQuantity(int quantity) {
        return quantity <= 0 && purchaseMaximumQuantity >= quantity && remainingQuantity >= quantity;
    }

    public void validateOnSaleStatus() {
        if (stockStatus.equals(ProductStockStatus.ON_SALES))
            throw ProductException.NOT_SALES_STATUS;
    }

    public void decrease(int quantity) {
        validateOnSaleStatus();

        if (!isValidPurchaseQuantity(quantity)) {
            throw ProductException.EXCEED_PURCHASE_PRODUCT_MAX_LIMIT;
        }

        remainingQuantity -= quantity;

        if (remainingQuantity < 0) {
            throw ProductException.NOT_ENOUGH_STOCK;
        }
    }

    public void increase(int quantity) {
        if (quantity < 0) {
            throw ProductException.INVALID_PRODUCT_QUANTITY;
        }

        remainingQuantity += quantity;
    }
}
