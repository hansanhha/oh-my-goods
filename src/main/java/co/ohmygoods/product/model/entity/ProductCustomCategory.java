package co.ohmygoods.product.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductCustomCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_custom_category_id")
    private CustomCategory customCategory;

    public static ProductCustomCategory create(Product product, CustomCategory customCategory) {
        var productDetailCategoryMapping = new ProductCustomCategory();
        productDetailCategoryMapping.product = product;
        productDetailCategoryMapping.customCategory = customCategory;
        return productDetailCategoryMapping;
    }
}
