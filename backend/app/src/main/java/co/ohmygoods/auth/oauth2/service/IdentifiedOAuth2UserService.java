package co.ohmygoods.auth.oauth2.service;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.service.dto.OAuth2SignUpRequest;
import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * <p>
 * IdentifiedOAuthUser를 반환하는  DefaultOAuth2UserService 구현체
 * oauth2 end-user에 대한 서비스 애플리케이션의 사용자 식별 값을 부여하기 위함
 * </p>
 *
 * <p>
 * 최초 oauth2 로그인한 사용자의 서비스 memberId를 생성함
 * 생성된 member id는 회원가입, 이후의 로그인에서 end-user의 식별 값으로 사용됨
 * </p>
 *
 * <oi>
 *  <li>{@link co.ohmygoods.auth.web.security.OAuth2AuthenticationSuccessHandler#onAuthenticationSuccess}</li>
 *  <li>{@link co.ohmygoods.auth.account.service.OAuth2AccountService#signUp}</li>
 *  <li>{@link CacheableOAuth2AuthorizedClientService}</li>
 * </oi>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class IdentifiedOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2AttributeService oAuth2AttributeService;
    private final AccountRepository accountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) super.loadUser(userRequest);

        String combinedOAuth2MemberId = oAuth2AttributeService.getCombinedOAuth2MemberId(OAuth2Provider.valueOf(
                userRequest.getClientRegistration().getRegistrationId().toUpperCase()), oAuth2User.getName());

        Optional<Account> account = accountRepository.findByOauth2MemberId(combinedOAuth2MemberId);

        return account
                .map(account_ -> new IdentifiedOAuthUser(oAuth2User, account_.getMemberId(), false))
                .orElseGet(() -> new IdentifiedOAuthUser(oAuth2User, UUID.randomUUID().toString(), true));
    }

}
