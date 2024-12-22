package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.jwt.model.vo.JwtProvider;
import co.ohmygoods.auth.jwt.service.dto.ValidationResult;
import co.ohmygoods.auth.jwt.service.dto.Jwts;

import java.util.Set;

public interface JwtService {

    Jwts generate(String email, Set<Role.Authority> scopes);

    Jwts regenerate(String email, String refreshTokenValue);

    void removeRefreshToken(String email);

    ValidationResult validateAccessToken(String accessToken);

    boolean isSupport(JwtProvider jwtProvider);
}
