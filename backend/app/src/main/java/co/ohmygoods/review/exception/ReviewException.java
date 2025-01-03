package co.ohmygoods.review.exception;

import co.ohmygoods.global.exception.DomainException;

public class ReviewException extends DomainException {

    public static final ReviewException NOT_FOUND_REVIEW_IMAGE = new ReviewException(ReviewError.NOT_FOUND_REVIEW_IMAGE);
    public static final ReviewException NOT_FOUND_REVIEW = new ReviewException(ReviewError.NOT_FOUND_REVIEW);
    public static final ReviewException NOT_FOUND_REVIEW_COMMENT = new ReviewException(ReviewError.NOT_FOUND_REVIEW_COMMENT);
    public static final ReviewException NOT_FOUND_REVIEW_REPLY = new ReviewException(ReviewError.NOT_FOUND_REVIEW_REPLY);

    public static final ReviewException INVALID_REVIEW_SCORE = new ReviewException(ReviewError.INVALID_REVIEW_SCORE);
    public static final ReviewException INVALID_REVIEW_CONTENT = new ReviewException(ReviewError.INVALID_REVIEW_CONTENT);
    public static final ReviewException INVALID_REVIEW_STATUS = new ReviewException(ReviewError.INVALID_REVIEW_STATUS);

    public static final ReviewException INVALID_AUTHORITY_WRITE_REVIEW = new ReviewException(ReviewError.INVALID_AUTHORITY_REVIEW);
    public static final ReviewException INVALID_AUTHORITY_WRITE_REVIEW_COMMENT = new ReviewException(ReviewError.INVALID_AUTHORITY_REVIEW_COMMENT);
    public static final ReviewException INVALID_AUTHORITY_WRITE_REVIEW_REPLY = new ReviewException(ReviewError.INVALID_AUTHORITY_REVIEW_REPLY);
    public static final ReviewException ALREADY_WRITTEN_REVIEW = new ReviewException(ReviewError.ALREADY_WRITTEN_REVIEW);

    public ReviewException(ReviewError reviewError) {
        super(reviewError);
    }

    public static ReviewException notFoundReview() {
        return NOT_FOUND_REVIEW;
    }

    public static ReviewException notFoundReviewComment() {
        return NOT_FOUND_REVIEW_COMMENT;
    }

    public static ReviewException notFoundReviewReply() {
        return NOT_FOUND_REVIEW_REPLY;
    }

    public static ReviewException notFoundReviewImage() {
        return NOT_FOUND_REVIEW_IMAGE;
    }
}
