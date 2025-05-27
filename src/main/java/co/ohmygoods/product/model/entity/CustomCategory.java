package co.ohmygoods.product.model.entity;

import co.ohmygoods.shop.model.entity.Shop;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class CustomCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(unique = true, nullable = false)
    private String name;

    public static CustomCategory create(Shop shop, String name) {
        var productDetailCategory = new CustomCategory();
        productDetailCategory.shop = shop;
        productDetailCategory.name = name;
        return productDetailCategory;
    }
}
