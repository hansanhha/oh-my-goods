package co.ohmygoods.auth.account.service;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.service.dto.AccountResponse;
import co.ohmygoods.auth.account.service.dto.OAuth2SignUpRequest;
import co.ohmygoods.auth.jwt.model.vo.JWTs;
import co.ohmygoods.auth.jwt.service.JWTService;
import co.ohmygoods.auth.web.security.oauth2.OAuth2AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static co.ohmygoods.auth.jwt.model.vo.JWTClaimsKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SignService {

    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final JWTService jwtService;
    private final AccountRepository accountRepository;

    public AccountResponse signUp(OAuth2SignUpRequest OAuth2SignUpRequest) {
        var newAccountInfo = Account.builder()
                .nickname(UUID.randomUUID().toString())
                .oauth2Vendor(OAuth2SignUpRequest.vendor())
                .oauth2MemberId(OAuth2SignUpRequest.oauth2MemberId())
                .email(OAuth2SignUpRequest.email())
                .role(Role.USER)
                .build();

        var newAccount = accountRepository.save(newAccountInfo);
        return AccountResponse.from(newAccount);
    }

    public JWTs signIn(String email, OAuth2AuthorizationService.OAuth2Vendor vendor, Role role) {
        return jwtService.generate(Map.of(SUBJECT, email, VENDOR, vendor.name(), ROLE, role.name()));
    }

    public JWTs reissueJWT(String refreshToken) {
        return jwtService.regenerate(refreshToken);
    }

    public void signOut(String accessToken) {
        var optionalJwtInfo = jwtService.extractTokenInfo(accessToken);

        optionalJwtInfo.ifPresent(jwtInfo -> {
            oAuth2AuthorizationService.signOut(jwtInfo);
            jwtService.revokeRefreshToken(accessToken);
        });
    }

    public boolean deleteAccount(String accessToken, String email) {
        var optionalAccount = accountRepository.findByEmail(email);
        var optionalJwtInfo = jwtService.extractTokenInfo(accessToken);

        if (optionalAccount.isEmpty() || optionalJwtInfo.isEmpty()) {
            return true;
        }

        var account = optionalAccount.get();
        var jwtInfo = optionalJwtInfo.get();

        if (!account.getEmail().equals(jwtInfo.subject())) {
            return false;
        }

        oAuth2AuthorizationService.unlink(jwtInfo);
        jwtService.revokeRefreshToken(accessToken);
        accountRepository.delete(account);

        return true;
    }
}
