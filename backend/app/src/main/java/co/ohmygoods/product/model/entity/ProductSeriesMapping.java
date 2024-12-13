package co.ohmygoods.product.model.entity;

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
    private ProductSeries series;

    public static ProductSeriesMapping toEntity(Product product, ProductSeries series) {
        var productSeriesMapping = new ProductSeriesMapping();
        productSeriesMapping.product = product;
        productSeriesMapping.series = series;
        return productSeriesMapping;
    }
}
