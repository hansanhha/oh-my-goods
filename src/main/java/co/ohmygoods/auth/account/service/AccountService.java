package co.ohmygoods.auth.account.service;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.service.dto.AccountMetadataResponse;
import co.ohmygoods.auth.account.service.dto.OAuth2AuthorizationResponse;
import co.ohmygoods.auth.account.service.dto.OAuth2SignUpRequest;
import co.ohmygoods.auth.account.service.dto.SignInResponse;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.auth.jwt.service.JwtService;
import co.ohmygoods.auth.jwt.service.dto.Jwts;
import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;
import co.ohmygoods.auth.oauth2.service.OAuth2AttributeService;
import co.ohmygoods.auth.oauth2.service.OAuth2AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final List<OAuth2AuthorizationService> oAuth2AuthorizationServices;
    private final OAuth2AttributeService oAuth2AttributeService;
    private final JwtService jwtService;
    private final AccountRepository accountRepository;

    public AccountMetadataResponse getAccountMetadata(String memberId) {
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);
        return AccountMetadataResponse.from(account);
    }

    public SignInResponse signIn(String memberId) {
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);
        Jwts generatedJwts = jwtService.generate(memberId, account.getRole());
        return new SignInResponse(generatedJwts.accessToken(), generatedJwts.refreshToken());
    }

    public SignInResponse refreshSignIn(String memberId, String refreshTokenValue) {
        Jwts regeneratedJwts = jwtService.regenerate(memberId, refreshTokenValue);
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

    public void delete(String memberId) {
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);

        OAuth2AuthorizationService oAuth2AuthorizationService = findSupportOAuth2AuthorizationService(account.getOauth2Provider());

        OAuth2AuthorizationResponse oAuth2UnlinkResponse = oAuth2AuthorizationService.unlink(account.getEmail());

        if (!oAuth2UnlinkResponse.isSuccess()) {
            throw AuthException.failedOAuth2Unlink(oAuth2UnlinkResponse.oauth2ProviderErrorCode(), oAuth2UnlinkResponse.oauth2ProviderErrorMsg());
        }

        jwtService.removeRefreshToken(memberId);
        accountRepository.delete(account);
    }

    public AccountMetadataResponse signUp(OAuth2SignUpRequest oAuth2SignUpRequest) {
        String combinedOAuth2MemberId = oAuth2AttributeService.getCombinedOAuth2MemberId(oAuth2SignUpRequest.oAuth2Provider(), oAuth2SignUpRequest.oauth2MemberId());

        var newAccount = Account.builder()
                .memberId(oAuth2SignUpRequest.memberId())
                .nickname(UUID.randomUUID().toString())
                .oauth2Provider(oAuth2SignUpRequest.oAuth2Provider())
                .oauth2MemberId(combinedOAuth2MemberId)
                .email(oAuth2SignUpRequest.email())
                .role(Role.USER)
                .build();

        var account = accountRepository.save(newAccount);
        return AccountMetadataResponse.from(account);
    }

    private OAuth2AuthorizationService findSupportOAuth2AuthorizationService(OAuth2Provider oAuth2Provider) {
        return oAuth2AuthorizationServices.stream()
                .filter(service -> service.canSupport(oAuth2Provider))
                .findFirst()
                .orElseThrow();
    }
}
