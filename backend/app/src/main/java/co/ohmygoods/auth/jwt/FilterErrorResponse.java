package co.ohmygoods.auth.jwt;

import java.time.Instant;

public record FilterErrorResponse(String message,
                                  Instant timestamp) {

    public static FilterErrorResponse of(String message, Instant timestamp) {
        return new FilterErrorRe            sponse(message, timestamp);
    }
}
