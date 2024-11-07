package co.ohmygoods.product.entity;

import co.ohmygoods.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductDetailCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(unique = true, nullable = false)
    private String detailCategory;
}
