package co.ohmygoods.product.exception;

public class InvalidProductSeriesException extends RuntimeException {
    public InvalidProductSeriesException(String message) {
        super(message);
    }

    public static InvalidProductSeriesException duplicateName(String seriesName) {
        return new InvalidProductSeriesException(seriesName);
    }
}
