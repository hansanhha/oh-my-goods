package co.ohmygoods.auth.account.service.dto;

import co.ohmygoods.auth.jwt.service.dto.TokenDTO;

public record SignInResponse(TokenDTO accessToken,
                             TokenDTO refreshToken) {
}
