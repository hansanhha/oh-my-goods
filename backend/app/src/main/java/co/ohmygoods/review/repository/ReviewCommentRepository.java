package co.ohmygoods.review.repository;

import co.ohmygoods.review.model.entity.ReviewComment;
import org.springframework.data.repository.CrudRepository;

public interface ReviewCommentRepository extends CrudRepository<ReviewComment, Long> {
}
