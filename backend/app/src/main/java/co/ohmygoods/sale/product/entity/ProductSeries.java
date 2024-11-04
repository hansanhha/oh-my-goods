package co.ohmygoods.sale.product.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductSeries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seriesName;
}
