package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.account.exception.AccountException;
import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.jwt.model.entity.RefreshToken;
import co.ohmygoods.auth.jwt.repository.RedisRefreshTokenRepository;
import co.ohmygoods.auth.jwt.service.dto.Jwts;
import co.ohmygoods.auth.jwt.service.dto.TokenDTO;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public abstract class AbstractJwtService implements JwtService {

    private final AccountRepository accountRepository;
    private final RedisRefreshTokenRepository refreshTokenRepository;

    @Override
    public Jwts generate(String memberId, Set<Role.Authority> scopes) {
        refreshTokenRepository.removeAllByMemberId(memberId);

        TokenDTO accessToken = generateAccessToken(memberId, scopes);
        TokenDTO refreshToken = generateRefreshToken(memberId);

        RefreshToken issuedRefreshToken = RefreshToken.create(memberId, refreshToken.tokenValue());

        refreshTokenRepository.save(issuedRefreshToken);

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
        RefreshToken refreshTokenInDB = refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(AccountException::new);

        // 토큰 탈취 감지
        if (refreshTokenValue.equals(refreshTokenInDB.getTokenValue())) {
            refreshTokenRepository.removeAllByMemberId(memberId);
            throw new AccountException();
        }

        Account account = accountRepository.findByEmail(memberId).orElseThrow(AccountException::new);

        TokenDTO accessToken = generateAccessToken(memberId, account.getRole().getAuthorities());
        TokenDTO refreshToken = generateRefreshToken(memberId);

        refreshTokenInDB.updateTokenValue(refreshToken.tokenValue());

        return new Jwts(accessToken, refreshToken);
    }

    @Override
    public void removeRefreshToken(String memberId) {
        refreshTokenRepository.removeAllByMemberId(memberId);
    }

    abstract protected TokenDTO generateAccessToken(String email, Set<Role.Authority> scopes);
    abstract protected TokenDTO generateRefreshToken(String email);
}
