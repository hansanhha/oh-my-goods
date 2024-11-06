package co.ohmygoods.product.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductSeries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String seriesName;
}
