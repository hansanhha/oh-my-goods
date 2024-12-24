package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.account.exception.AccountException;
import co.ohmygoods.auth.jwt.model.entity.RefreshToken;
import co.ohmygoods.auth.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CacheableRefreshTokenService {

    private static final String REDIS_CACHE_NAMES = "refreshToken";

    private final RefreshTokenRepository refreshTokenRepository;

    @Cacheable(cacheNames = REDIS_CACHE_NAMES, key = "#memberId")
    public RefreshToken getRefreshToken(String memberId) {
        return refreshTokenRepository.findByMemberId(memberId).orElseThrow(AccountException::new);
    }

    @CacheEvict(cacheNames = REDIS_CACHE_NAMES, key = "#memberId")
    public void removeAllRefreshToken(String memberId) {
        refreshTokenRepository.removeAllByMemberId(memberId);
    }

    @CachePut(cacheNames = REDIS_CACHE_NAMES, key = "#issuedRefreshToken.memberId")
    public void save(RefreshToken issuedRefreshToken) {
        refreshTokenRepository.save(issuedRefreshToken);
    }
}
