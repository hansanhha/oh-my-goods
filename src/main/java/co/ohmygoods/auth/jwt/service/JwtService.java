package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.jwt.model.vo.JWTProvider;
import co.ohmygoods.auth.jwt.service.dto.JWTs;

public interface JWTService {

    JWTs generateToken(String memberId, Role role);

    JWTs regenerate(String memberId, String refreshTokenValue);

    void removeRefreshToken(String memberId);

    boolean isSupport(JWTProvider jwtProvider);
}
