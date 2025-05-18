package co.ohmygoods.auth.jwt.service.dto;

import java.time.Instant;

import co.ohmygoods.auth.jwt.model.vo.TokenType;

public record JWT(String tokenValue,
                  TokenType tokenType,
                  Instant expiresIn) {
}
