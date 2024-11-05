package co.ohmygoods.domain.jwt.vo;

import co.ohmygoods.domain.account.vo.Role;
import co.ohmygoods.domain.oauth2.vo.OAuth2Vendor;
import lombok.Builder;

import java.time.Instant;

@Builder
public record JWTInfo(String tokenValue,
                      String jwtId,
                      String subject,
                      OAuth2Vendor oAuth2Vendor,
                      Role role,
                      String issuer,
                      String audience,
                      Instant issuedAt,
                      Instant expiresIn) {

}
