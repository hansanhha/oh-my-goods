package co.ohmygoods.sale.product.entity;

import co.ohmygoods.global.jpa.BaseEntity;
import co.ohmygoods.sale.product.exception.ProductShopCheckException;
import co.ohmygoods.sale.product.vo.ProductCategory;
import co.ohmygoods.sale.product.vo.ProductStatus;
import co.ohmygoods.sale.product.vo.ProductType;
import co.ohmygoods.sale.shop.entity.Shop;
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
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Setter
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSeriesMapping> productSeriesMappings;

    @Setter
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetailCategoryMapping> productDetailCategoryMappings;

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
                               ProductCategory category,
                               List<ProductDetailCategoryMapping> productDetailCategoryMappings,
                               List<ProductSeriesMapping> productSeriesMappings) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.category = category;
        this.productDetailCategoryMappings = productDetailCategoryMappings;
        this.productSeriesMappings = productSeriesMappings;
    }
}
