package co.ohmygoods.cart.model.entity;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.cart.exception.CartException;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.product.model.entity.Product;
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
    private Account account;

    private int quantity;

    private int originalPriceWhenPutIn;

    public static Cart create(Product product, Account account) {
        var cart = new Cart();
        cart.product = product;
        cart.account = account;
        cart.quantity = 1;
        cart.originalPriceWhenPutIn = product.getOriginalPrice();
        return cart;
    }

    public void updateQuantity(int quantity) {
        if (quantity <= 0) {
            throw CartException.INVALID_QUANTITY;
        }

        this.quantity = quantity;
    }
}