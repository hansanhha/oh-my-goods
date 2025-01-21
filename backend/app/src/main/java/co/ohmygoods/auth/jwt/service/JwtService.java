package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.jwt.model.vo.JwtProvider;
import co.ohmygoods.auth.jwt.service.dto.Jwts;

import java.util.Set;

public interface JwtService {

    Jwts generate(String memberId, Role role);

    Jwts regenerate(String memberId, String refreshTokenValue);

    void removeRefreshToken(String memberId);

    boolean isSupport(JwtProvider jwtProvider);
}
