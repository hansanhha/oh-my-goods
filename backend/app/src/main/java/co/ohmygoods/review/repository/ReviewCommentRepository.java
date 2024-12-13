package co.ohmygoods.review.repository;

import co.ohmygoods.review.model.entity.ReviewComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ReviewCommentRepository extends CrudRepository<ReviewComment, Long> {

    @Query("SELECT rc " +
            "FROM ReviewComment rc " +
            "JOIN FETCH rc.account " +
            "WHERE rc.review.id = :reviewId")
    Slice<ReviewComment> fetchWriterAllByReviewId(Long reviewId, Pageable pageable);

    @Query("SELECT rc " +
            "FROM ReviewComment rc " +
            "JOIN FETCH rc.account " +
            "WHERE rc.review.id = :reviewId " +
            "AND rc.parentReviewComment.id = :reviewCommentId")
    Slice<ReviewComment> fetchWriterAllByReviewIdAndReviewCommentId(Long reviewId, Long reviewCommentId, Pageable pageable);
}
