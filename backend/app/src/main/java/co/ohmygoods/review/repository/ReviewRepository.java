package co.ohmygoods.review.repository;

import co.ohmygoods.order.entity.Order;
import co.ohmygoods.review.model.entity.Review;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ReviewRepository extends CrudRepository<Review, Long> {

    Optional<Review> findReviewByOrder(Order order);
}
