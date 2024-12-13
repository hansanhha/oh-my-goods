package co.ohmygoods.product.model.entity;

import co.ohmygoods.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductCustomCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(unique = true, nullable = false)
    private String customCategoryName;

    public static ProductCustomCategory toEntity(Shop shop, String customCategoryName) {
        var productDetailCategory = new ProductCustomCategory();
        productDetailCategory.shop = shop;
        productDetailCategory.customCategoryName = customCategoryName;
        return productDetailCategory;
    }
}
