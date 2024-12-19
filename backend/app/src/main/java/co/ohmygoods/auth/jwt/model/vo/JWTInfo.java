package co.ohmygoods.auth.jwt.model.vo;

import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.web.security.oauth2.OAuth2AuthorizationService;
import lombok.Builder;

import java.time.Instant;

@Builder
public record JWTInfo(String tokenValue,
                      String jwtId,
                      String subject,
                      OAuth2AuthorizationService.OAuth2Vendor oAuth2Vendor,
                      Role role,
                      String issuer,
                      String audience,
                      Instant issuedAt,
                      Instant expiresIn) {

}
