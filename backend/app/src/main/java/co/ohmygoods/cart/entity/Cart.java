package co.ohmygoods.cart.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.cart.exception.CartException;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private OAuth2Account account;

    private int quantity;

    private int originalPriceWhenPutIn;

    public static Cart toEntity(Product product, OAuth2Account account) {
        var cart = new Cart();
        cart.product = product;
        cart.account = account;
        cart.quantity = 1;
        cart.originalPriceWhenPutIn = product.getOriginalPrice();
        return cart;
    }

    public void updateQuantity(int quantity) {
        if (quantity <= 0) {
            throw CartException.invalidQuantity(id, quantity);
        }

        this.quantity = quantity;
    }
}