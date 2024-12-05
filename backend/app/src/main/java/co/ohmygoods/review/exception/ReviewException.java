package co.ohmygoods.review.exception;

public class ReviewException extends RuntimeException {

    public ReviewException() {
    }

    public ReviewException(String message) {
        super(message);
    }

    public static ReviewException notFoundAccount() {
        return new ReviewException();
    }

    public static ReviewException notFoundOrder() {
        return new ReviewException();
    }

    public static ReviewException notFoundReviewComment() {
        return new ReviewException();
    }

    public static ReviewException notFoundReview() {
        return new ReviewException();
    }

    public static ReviewException alreadyWriteReview() {
        return new ReviewException();
    }

    public static ReviewException invalidReviewAuthority() {
        return new ReviewException();
    }

    public static ReviewException invalidReviewCommentAuthority() {
        return new ReviewException();
    }

}
