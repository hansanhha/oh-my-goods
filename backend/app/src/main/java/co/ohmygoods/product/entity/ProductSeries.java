package co.ohmygoods.product.entity;

import co.ohmygoods.product.dto.ProductSeriesDto;
import co.ohmygoods.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductSeries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(unique = true, nullable = false)
    private String seriesName;

    public static ProductSeries toEntity(Shop shop, String seriesName) {
        var productSeries = new ProductSeries();
        productSeries.shop = shop;
        productSeries.seriesName = seriesName;
        return productSeries;
    }
}
