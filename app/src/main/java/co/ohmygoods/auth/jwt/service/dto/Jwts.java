package co.ohmygoods.auth.jwt.service.dto;

public record Jwts(TokenDTO accessToken,
                   TokenDTO refreshToken) {
}
