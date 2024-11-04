package co.ohmygoods.sale.product.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductSeriesMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_series_id")
    private ProductSeries productSeries;

    public static ProductSeriesMapping toEntity(Product product, ProductSeries series) {
        var productSeriesMapping = new ProductSeriesMapping();
        productSeriesMapping.product = product;
        productSeriesMapping.productSeries = series;
        return productSeriesMapping;
    }
}
