package co.ohmygoods.review.model.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.global.entity.LikeBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewComment extends LikeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private OAuth2Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_review_comment__id")
    private ReviewComment parentReviewComment;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public static ReviewComment write(Review review, OAuth2Account account, String content) {
        return new ReviewComment(0L, review, account, null, content);
    }

    public static ReviewComment reply(ReviewComment reviewComment, OAuth2Account account, String content) {
        return new ReviewComment(0L, reviewComment.getReview(), account, reviewComment, content);
    }

    public void update(String content) {
        this.content = content;
    }
}
