package co.ohmygoods.auth.account.service.dto;

import co.ohmygoods.auth.jwt.service.dto.JWT;

public record SignInResponse(JWT accessToken,
                             JWT refreshToken) {
}
