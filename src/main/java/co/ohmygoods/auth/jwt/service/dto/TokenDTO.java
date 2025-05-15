package co.ohmygoods.auth.jwt.service.dto;

import co.ohmygoods.auth.jwt.model.vo.TokenType;

import java.time.Duration;

public record TokenDTO(String tokenValue,
                       TokenType tokenType,
                       Duration expiresIn) {
}
