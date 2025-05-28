package co.ohmygoods.review.repository;

import co.ohmygoods.review.model.entity.Review;
import co.ohmygoods.review.model.entity.ReviewImage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReviewImageRepository extends CrudRepository<ReviewImage, Long> {

    List<ReviewImage> findAllByReview(Review review);
}
