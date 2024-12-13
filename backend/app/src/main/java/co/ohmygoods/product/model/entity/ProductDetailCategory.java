package co.ohmygoods.product.model.entity;

import co.ohmygoods.product.model.vo.ProductTopCategory;
import co.ohmygoods.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductDetailCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Enumerated(EnumType.STRING)
    private ProductTopCategory topCategory;

    @Column(unique = true, nullable = false)
    private String categoryName;

    public static ProductDetailCategory toEntity(Shop shop, ProductTopCategory topCategory, String categoryName) {
        var productDetailCategory = new ProductDetailCategory();
        productDetailCategory.shop = shop;
        productDetailCategory.topCategory = topCategory;
        productDetailCategory.categoryName = categoryName;
        return productDetailCategory;
    }
}
