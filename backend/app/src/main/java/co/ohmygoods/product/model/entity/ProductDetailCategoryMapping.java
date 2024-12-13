package co.ohmygoods.product.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductDetailCategoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_category_id")
    private ProductDetailCategory detailCategory;

    public static ProductDetailCategoryMapping toEntity(Product product, ProductDetailCategory detailCategory) {
        var productDetailCategoryMapping = new ProductDetailCategoryMapping();
        productDetailCategoryMapping.product = product;
        productDetailCategoryMapping.detailCategory = detailCategory;
        return productDetailCategoryMapping;
    }
}
