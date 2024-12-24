package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.account.exception.AccountException;
import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.jwt.model.entity.RefreshToken;
import co.ohmygoods.auth.jwt.service.dto.Jwts;
import co.ohmygoods.auth.jwt.service.dto.TokenDTO;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * jwt 라이브러리 별 공통 로직 추상화 JwtService 기본 구현체
 * <oi>
 *  <li>Refresh Token 관리(CacheableRefreshTokenService 위임)</li>
 *  <li>accessToken, refreshToken 생성 템플릿 메서드 정의</li>
 * </oi>
 */
@RequiredArgsConstructor
public abstract class AbstractJwtService implements JwtService {

    private final AccountRepository accountRepository;
    private final CacheableRefreshTokenService refreshTokenService;

    @Override
    public Jwts generate(String memberId, Set<Role.Authority> scopes) {
        refreshTokenService.removeAllRefreshToken(memberId);

        TokenDTO accessToken = generateAccessToken(memberId, scopes);
        TokenDTO refreshToken = generateRefreshToken(memberId);

        RefreshToken issuedRefreshToken = RefreshToken.create(memberId, refreshToken.tokenValue());

        refreshTokenService.save(issuedRefreshToken);

        return new Jwts(accessToken, refreshToken);
    }

    /**
     * <p>Refresh Token Rotation 방식 사용</p>
     * <p>사용자의 Refresh Token 탈취 감지: 전달받은 토큰 값과 DB에 저장된 토큰 값 비교</p>
     *
     * @param refreshTokenValue: 클라이언트에게 전달받은 서명된 refresh token 값
     */
    @Override
    public Jwts regenerate(String memberId, String refreshTokenValue) {
        RefreshToken refreshTokenInDB = refreshTokenService.getRefreshToken(memberId);

        // 토큰 탈취 감지
        if (!refreshTokenValue.equals(refreshTokenInDB.getTokenValue())) {
            refreshTokenService.removeAllRefreshToken(memberId);
            throw new AccountException();
        }

        Account account = accountRepository.findByEmail(memberId).orElseThrow(AccountException::new);

        TokenDTO accessToken = generateAccessToken(memberId, account.getRole().getAuthorities());
        TokenDTO refreshToken = generateRefreshToken(memberId);

        refreshTokenInDB.updateTokenValue(refreshToken.tokenValue());

        refreshTokenService.save(refreshTokenInDB);

        return new Jwts(accessToken, refreshToken);
    }

    @Override
    public void removeRefreshToken(String memberId) {
        refreshTokenService.removeAllRefreshToken(memberId);
    }

    abstract protected TokenDTO generateAccessToken(String email, Set<Role.Authority> scopes);
    abstract protected TokenDTO generateRefreshToken(String email);
}
