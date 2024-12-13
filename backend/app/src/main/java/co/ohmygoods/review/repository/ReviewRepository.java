package co.ohmygoods.review.repository;

import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.review.model.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ReviewRepository extends CrudRepository<Review, Long> {

    Optional<Review> findReviewByOrder(Order order);

    @Query("SELECT r " +
            "FROM Review r " +
            "JOIN FETCH r.reviewer " +
            "WHERE r.product.id = :productId")
    Slice<Review> fetchReviewerAllByProductId(Long productId, Pageable pageable);
}
