package co.ohmygoods.product.entity;

import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.product.exception.InvalidProductUpdateParameterException;
import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.exception.ProductShopCheckException;
import co.ohmygoods.product.exception.ProductStockStatusException;
import co.ohmygoods.product.vo.ProductTopCategory;
import co.ohmygoods.product.vo.ProductStockStatus;
import co.ohmygoods.product.vo.ProductType;
import co.ohmygoods.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private ProductTopCategory topCategory;

    @Enumerated(EnumType.STRING)
    private ProductStockStatus stockStatus;

    @Setter
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSeriesMapping> seriesMappings;

    @Setter
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetailCategoryMapping> detailCategoryMappings;

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

    public void updateMetadata(String name,
                               String description,
                               ProductType type,
                               ProductTopCategory category,
                               List<ProductDetailCategoryMapping> productDetailCategoryMappings,
                               List<ProductSeriesMapping> productSeriesMappings) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.topCategory = category;
        this.detailCategoryMappings = productDetailCategoryMappings;
        this.seriesMappings = productSeriesMappings;
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

    public boolean isInvalidRequestQuantity(int quantity) {
        return purchaseMaximumQuantity > quantity && remainingQuantity > quantity;
    }

    public boolean isOnSold() {
        return stockStatus.equals(ProductStockStatus.ON_SALES);
    }

    public void decrease(int quantity) {
        if (!isInvalidRequestQuantity(quantity)) {
            ProductException.throwCauseInvalidDecreaseQuantity(purchaseMaximumQuantity, remainingQuantity, quantity);
        }

        if (!stockStatus.equals(ProductStockStatus.ON_SALES)) {
            ProductStockStatusException.throwInvalidStatus(stockStatus);
        }

        remainingQuantity -= quantity;
    }

    public void increase(int quantity) {
        if (quantity < 0) {
            ProductException.throwCauseInvalidIncreaseQuantity(quantity);
        }

        remainingQuantity += quantity;
    }
}
