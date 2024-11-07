package co.ohmygoods.product.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductFlexibleCategoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_flexible_category_id")
    private ProductFlexibleCategory productFlexibleCategory;

    public static ProductFlexibleCategoryMapping toEntity(Product product, ProductFlexibleCategory detailCategory) {
        var productDetailCategoryMapping = new ProductFlexibleCategoryMapping();
        productDetailCategoryMapping.product = product;
        productDetailCategoryMapping.productFlexibleCategory = detailCategory;
        return productDetailCategoryMapping;
    }
}
