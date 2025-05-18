package co.ohmygoods.auth.jwt.service;


import java.time.Duration;


public interface RefreshTokenService {

    void validateStealToken(String memberId, String refreshTokenValue);

    void save(String memberId, String refreshTokenValue, Duration expiresIn);

    void remove(String memberId);

}
