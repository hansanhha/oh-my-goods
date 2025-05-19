package co.ohmygoods.auth.oauth2.service;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.service.SignInService;
import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;
import co.ohmygoods.auth.security.OAuth2AuthenticationSuccessHandler;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
 *  <li>{@link OAuth2AuthenticationSuccessHandler#onAuthenticationSuccess}</li>
 *  <li>{@link SignInService#signUp}</li>
 * </oi>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2UserLoginService extends DefaultOAuth2UserService {

    private final OAuth2UserAttributeUtils oAuth2UserAttributeUtils;
    private final AccountRepository accountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) super.loadUser(userRequest);

        String uniqueOAuth2MemberId = oAuth2UserAttributeUtils.getUniqueOAuth2MemberId(OAuth2Provider.valueOf(
                userRequest.getClientRegistration().getRegistrationId().toUpperCase()), oAuth2User.getName());

        Optional<Account> existingAccount = accountRepository.findByOauth2MemberId(uniqueOAuth2MemberId);

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return existingAccount
                .map(account -> new LoggedOAuthUser(oAuth2User, account.getMemberId(), false, userNameAttributeName))
                .orElseGet(() -> new LoggedOAuthUser(oAuth2User, UUID.randomUUID().toString(), true, userNameAttributeName));
    }

}
