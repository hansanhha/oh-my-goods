package co.ohmygoods.auth.account;

import co.ohmygoods.auth.account.dto.OAuth2SignUpRequest;
import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.vo.Role;
import co.ohmygoods.auth.jwt.JWTService;
import co.ohmygoods.auth.jwt.vo.JWTClaimsKey;
import co.ohmygoods.auth.jwt.vo.JWTs;
import co.ohmygoods.auth.oauth2.OAuth2AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2SignService {

    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final JWTService jwtService;
    private final AccountRepository accountRepository;

    public Optional<Long> findIdByEmail(String email) {
        return accountRepository.findByEmail(email).map(OAuth2Account::getId);
    }

    public Long signUp(OAuth2SignUpRequest OAuth2SignUpRequest) {
        var newAccountInfo = OAuth2Account.builder()
                .nickname(UUID.randomUUID().toString())
                .oauth2Vendor(OAuth2SignUpRequest.vendor())
                .oauth2MemberId(OAuth2SignUpRequest.oauth2MemberId())
                .email(OAuth2SignUpRequest.email())
                .role(Role.USER)
                .build();

        var newAccount = accountRepository.save(newAccountInfo);
        return newAccount.getId();
    }

    public JWTs signIn(String email) {
        return jwtService.generate(Map.of(JWTClaimsKey.SUBJECT, email));
    }

    public void signOut(String accessToken) {
        var optionalJwtInfo = jwtService.extractTokenInfo(accessToken);

        optionalJwtInfo.ifPresent(jwtInfo -> {
            oAuth2AuthorizationService.signOut(jwtInfo.subject());
            jwtService.revokeRefreshToken(accessToken);
        });
    }

    public void deleteAccount(String accessToken, String email) {
        var optionalAccount = accountRepository.findByEmail(email);
        var optionalJwtInfo = jwtService.extractTokenInfo(accessToken);

        if (optionalAccount.isEmpty() || optionalJwtInfo.isEmpty()) {
            return;
        }

        var account = optionalAccount.get();
        var jwtInfo = optionalJwtInfo.get();

        oAuth2AuthorizationService.unlink(jwtInfo.subject());
        jwtService.revokeRefreshToken(accessToken);
        accountRepository.delete(account);
    }
}
