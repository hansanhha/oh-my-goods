package co.ohmygoods.auth.jwt.service.dto;

public record JWTs(JWT accessToken,
                   JWT refreshToken) {
}
