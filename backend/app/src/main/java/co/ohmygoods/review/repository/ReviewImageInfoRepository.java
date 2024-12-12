package co.ohmygoods.review.repository;

import co.ohmygoods.review.model.entity.Review;
import co.ohmygoods.review.model.entity.ReviewImageInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReviewImageInfoRepository extends CrudRepository<ReviewImageInfo, Long> {

    List<ReviewImageInfo> findAllByReview(Review review);
}
