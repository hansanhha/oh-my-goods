package co.ohmygoods.review.model.entity;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.global.entity.LikeBaseEntity;
import co.ohmygoods.order.model.entity.OrderItem;
import co.ohmygoods.product.model.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Review extends LikeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderItem orderItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private Account reviewer;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int starRating;

    public static Review create(OrderItem orderItem, Product product, Account reviewer, String content, int starRating) {
        if (starRating <= 0) {
            starRating = 1;
        } else if (starRating > 5) {
            starRating = 5;
        }

        return new Review(0L, orderItem, product, reviewer, content, starRating);
    }

    public boolean isNotReviewer(Account account) {
        return !reviewer.getEmail().equals(account.getEmail());
    }

    public void update(int starRating, String content) {
        this.starRating = starRating;
        this.content = content;
    }

}
