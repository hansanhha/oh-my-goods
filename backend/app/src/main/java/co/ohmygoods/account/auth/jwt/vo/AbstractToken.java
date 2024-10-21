package co.ohmygoods.account.auth.jwt.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public abstract class AbstractToken {

    private final String tokenValue;
    private final String subject;
    private final String jwtId;
    private final String issuer;
    private final String audience;
    private final Instant issuedAt;
    private final Instant expiresIn;
}
