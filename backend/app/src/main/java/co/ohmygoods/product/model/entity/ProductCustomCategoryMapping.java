package co.ohmygoods.product.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductCustomCategoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_custom_category_id")
    private ProductCustomCategory customCategory;

    public static ProductCustomCategoryMapping toEntity(Product product, ProductCustomCategory customCategory) {
        var productDetailCategoryMapping = new ProductCustomCategoryMapping();
        productDetailCategoryMapping.product = product;
        productDetailCategoryMapping.customCategory = customCategory;
        return productDetailCategoryMapping;
    }
}
