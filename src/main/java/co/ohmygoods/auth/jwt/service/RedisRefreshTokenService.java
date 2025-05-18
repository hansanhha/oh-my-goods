package co.ohmygoods.auth.jwt.service;


import co.ohmygoods.auth.exception.AuthException;

import java.time.Duration;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RedisRefreshTokenService implements RefreshTokenService {

    private static final String KEY_PREFIX = "auth:refresh:";

    private final RedisTemplate<String, Object> redis;

    public void validateStealToken(String memberId, String refreshTokenValue) {
        String storedRefreshTokenValue = String.valueOf(redis.opsForValue().get(getKey(memberId)));

        if (!refreshTokenValue.equals(storedRefreshTokenValue)) {
            remove(memberId);
            throw AuthException.STOLEN_JWT;
        }
    }

    public void save(String memberId, String refreshTokenValue, Duration expiresIn) {
        redis.opsForValue().set(getKey(memberId), refreshTokenValue, expiresIn);
    }

    public void remove(String memberId) {
        redis.delete(getKey(memberId));
    }

    private String getKey(String memberId) {
        return KEY_PREFIX.concat(memberId);
    }
    
}
