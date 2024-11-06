package co.ohmygoods.product.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductDetailCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String detailCategory;
}
