package co.ohmygoods.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductDetailCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String detailCategory;
}
