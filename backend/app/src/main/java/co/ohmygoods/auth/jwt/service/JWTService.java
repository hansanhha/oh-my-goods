package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.jwt.model.vo.JwtProvider;
import co.ohmygoods.auth.jwt.service.dto.ValidationResult;
import co.ohmygoods.auth.jwt.service.dto.Jwts;

import java.util.Set;

public interface JwtService {

    Jwts generate(String memberId, Set<Role.Authority> scopes);

    Jwts regenerate(String memberId, String refreshTokenValue);

    void removeRefreshToken(String memberId);

    ValidationResult validateAccessToken(String accessToken);

    boolean isSupport(JwtProvider jwtProvider);
}
