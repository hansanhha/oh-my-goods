package co.ohmygoods.auth.jwt.vo;

import lombok.Builder;

import java.time.Instant;

@Builder
public record JWTInfo(String jwtId,
                      String subject,
                      String role,
                      String issuer,
                      String audience,
                      Instant issuedAt,
                      Instant expiresIn) {

}
