package co.ohmygoods.auth.account.service;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.service.dto.AccountProfile;
import co.ohmygoods.auth.account.service.dto.OAuth2AuthorizationResponse;
import co.ohmygoods.auth.account.service.dto.OAuth2SignUpRequest;
import co.ohmygoods.auth.account.service.dto.SignInResponse;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.auth.jwt.service.JWTService;
import co.ohmygoods.auth.jwt.service.dto.JWTs;
import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;
import co.ohmygoods.auth.oauth2.service.OAuth2AttributeExtractor;
import co.ohmygoods.auth.oauth2.service.OAuth2AuthorizationService;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class SignInService {

    private final List<OAuth2AuthorizationService> oAuth2AuthorizationServices;
    private final OAuth2AttributeExtractor oAuth2AuttributeExtractor;
    private final JWTService jwtService;
    private final AccountRepository accountRepository;

    public SignInResponse signIn(String memberId) {
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);
        JWTs jwts = jwtService.generateToken(memberId, account.getRole());
        return new SignInResponse(jwts.accessToken(), jwts.refreshToken());
    }

    public SignInResponse refreshAccessToken(String memberId, String refreshTokenValue) {
        JWTs regeneratedJwts = jwtService.regenerate(memberId, refreshTokenValue);
        return new SignInResponse(regeneratedJwts.accessToken(), regeneratedJwts.refreshToken());
    }

    public void signOut(String memberId) {
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);

        OAuth2AuthorizationService oAuth2AuthorizationService = findSupportOAuth2AuthorizationService(account.getOauth2Provider());

        OAuth2AuthorizationResponse oAuth2signOutResponse = oAuth2AuthorizationService.signOut(account.getEmail());

        if (!oAuth2signOutResponse.isSuccess()) {
            throw AuthException.failedOAuth2SignOut(oAuth2signOutResponse.oauth2ProviderErrorCode(), oAuth2signOutResponse.oauth2ProviderErrorMsg());
        }

        jwtService.removeRefreshToken(memberId);
    }

    public AccountProfile signUp(OAuth2SignUpRequest oAuth2SignUpRequest) {
        String combinedOAuth2MemberId = oAuth2AuttributeExtractor.getCombinedOAuth2MemberId(oAuth2SignUpRequest.oAuth2Provider(), oAuth2SignUpRequest.oauth2MemberId());

        var newAccount = Account.builder()
                .memberId(oAuth2SignUpRequest.memberId())
                .nickname(UUID.randomUUID().toString())
                .oauth2Provider(oAuth2SignUpRequest.oAuth2Provider())
                .oauth2MemberId(combinedOAuth2MemberId)
                .email(oAuth2SignUpRequest.email())
                .role(Role.USER)
                .build();

        var account = accountRepository.save(newAccount);
        return AccountProfile.from(account);
    }

    private OAuth2AuthorizationService findSupportOAuth2AuthorizationService(OAuth2Provider oAuth2Provider) {
        return oAuth2AuthorizationServices.stream()
                .filter(service -> service.canSupport(oAuth2Provider))
                .findFirst()
                .orElseThrow();
    }
}
