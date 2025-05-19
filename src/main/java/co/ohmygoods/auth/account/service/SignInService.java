package co.ohmygoods.auth.account.service;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.service.dto.AccountProfile;
import co.ohmygoods.auth.account.service.dto.OAuth2SignUpRequest;
import co.ohmygoods.auth.account.service.dto.SignInResponse;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.auth.jwt.service.JWTService;
import co.ohmygoods.auth.jwt.service.dto.JWTs;
import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;
import co.ohmygoods.auth.oauth2.service.OAuth2UserAttributeUtils;
import co.ohmygoods.auth.oauth2.service.OAuth2APIService;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class SignInService {

    private final List<OAuth2APIService> oAuth2APIServices;
    private final OAuth2UserAttributeUtils oAuth2AuttributeExtractor;
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

        OAuth2APIService oAuth2APIService = findSupportOAuth2AuthorizationService(account.getOauth2Provider());

        oAuth2APIService.signOut(account.getEmail());

        jwtService.removeRefreshToken(memberId);
    }

    public AccountProfile signUp(OAuth2SignUpRequest oAuth2SignUpRequest) {
        String combinedOAuth2MemberId = oAuth2AuttributeExtractor.getUniqueOAuth2MemberId(oAuth2SignUpRequest.oAuth2Provider(), oAuth2SignUpRequest.oauth2MemberId());

        Account newAccount = Account.builder()
                .memberId(oAuth2SignUpRequest.memberId())
                .nickname(UUID.randomUUID().toString())
                .oauth2Provider(oAuth2SignUpRequest.oAuth2Provider())
                .oauth2MemberId(combinedOAuth2MemberId)
                .email(oAuth2SignUpRequest.email())
                .role(Role.USER)
                .build();

        Account account = accountRepository.save(newAccount);
        return AccountProfile.from(account);
    }

    private OAuth2APIService findSupportOAuth2AuthorizationService(OAuth2Provider oAuth2Provider) {
        return oAuth2APIServices.stream()
                .filter(service -> service.isSupport(oAuth2Provider))
                .findFirst()
                .orElseThrow();
    }
}
