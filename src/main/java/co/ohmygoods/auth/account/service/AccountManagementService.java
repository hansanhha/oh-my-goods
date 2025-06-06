package co.ohmygoods.auth.account.service;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.auth.jwt.service.JWTService;
import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;
import co.ohmygoods.auth.oauth2.service.OAuth2APIService;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountManagementService {

    private final List<OAuth2APIService> oAuth2APIServices;
    private final JWTService jwtService;
    private final AccountRepository accountRepository;


    public void delete(String memberId) {
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);

        OAuth2APIService oAuth2APIService = findSupportOAuth2AuthorizationService(account.getOauth2Provider());

        oAuth2APIService.unlink(account.getEmail());

        jwtService.removeRefreshToken(memberId);
        accountRepository.delete(account);
    }

    private OAuth2APIService findSupportOAuth2AuthorizationService(OAuth2Provider oAuth2Provider) {
        return oAuth2APIServices.stream()
                .filter(service -> service.isSupport(oAuth2Provider))
                .findFirst()
                .orElseThrow();
    }

}
