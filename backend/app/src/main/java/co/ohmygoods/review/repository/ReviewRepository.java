package co.ohmygoods.review.repository;

import co.ohmygoods.review.model.entity.Review;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<Review, Long> {
}
