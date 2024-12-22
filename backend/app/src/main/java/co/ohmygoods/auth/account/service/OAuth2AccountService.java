package co.ohmygoods.auth.account.service;

import co.ohmygoods.auth.account.exception.AccountException;
import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.service.dto.AccountResponse;
import co.ohmygoods.auth.account.service.dto.OAuth2AuthorizationResponse;
import co.ohmygoods.auth.account.service.dto.OAuth2SignUpRequest;
import co.ohmygoods.auth.account.service.dto.SignInResponse;
import co.ohmygoods.auth.jwt.service.JwtService;
import co.ohmygoods.auth.jwt.service.dto.Jwts;
import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;
import co.ohmygoods.auth.oauth2.service.OAuth2AttributeService;
import co.ohmygoods.auth.oauth2.service.OAuth2AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2AccountService {

    private final List<OAuth2AuthorizationService> oAuth2AuthorizationServices;
    private final OAuth2AttributeService oAuth2AttributeService;
    private final JwtService jwtService;
    private final AccountRepository accountRepository;

    public Optional<AccountResponse> getAccount(String email) {
        return accountRepository.findByEmail(email).map(AccountResponse::from);
    }

    public SignInResponse signIn(String email, Role role) {
        Jwts generatedJwts = jwtService.generate(email, role.getAuthorities());
        return new SignInResponse(generatedJwts.accessToken(), generatedJwts.refreshToken());
    }

    public SignInResponse refreshSignIn(String email, String refreshTokenValue) {
        Jwts regeneratedJwts = jwtService.regenerate(email, refreshTokenValue);
        return new SignInResponse(regeneratedJwts.accessToken(), regeneratedJwts.refreshToken());
    }

    public void signOut(String email) {
        jwtService.removeRefreshToken(email);
    }

    public void deleteAccount(String email) {
        Account account = accountRepository.findByEmail(email).orElseThrow(AccountException::new);

        OAuth2AuthorizationService oAuth2AuthorizationService = findSupportOAuth2AuthorizationService(account.getOauth2Provider());

        OAuth2AuthorizationResponse unlinkResponse = oAuth2AuthorizationService.unlink(email);

        if (!unlinkResponse.isSuccess()) {
            throw new AccountException();
        }

        jwtService.removeRefreshToken(email);
        accountRepository.delete(account);
    }

    public AccountResponse signUp(OAuth2SignUpRequest oAuth2SignUpRequest) {
        String oauth2MemberId = oAuth2AttributeService.getCombinedOAuth2MemberId(oAuth2SignUpRequest.oAuth2Provider(), oAuth2SignUpRequest.oauth2MemberId());

        var newAccount = Account.builder()
                .nickname(UUID.randomUUID().toString())
                .oauth2Provider(oAuth2SignUpRequest.oAuth2Provider())
                .oauth2MemberId(oauth2MemberId)
                .email(oAuth2SignUpRequest.email())
                .role(Role.USER)
                .build();

        var account = accountRepository.save(newAccount);
        return AccountResponse.from(account);
    }

    private OAuth2AuthorizationService findSupportOAuth2AuthorizationService(OAuth2Provider oAuth2Provider) {
        return oAuth2AuthorizationServices.stream()
                .filter(service -> service.canSupport(oAuth2Provider))
                .findFirst()
                .orElseThrow();
    }
}
