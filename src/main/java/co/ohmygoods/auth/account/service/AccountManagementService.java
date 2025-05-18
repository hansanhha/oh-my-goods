package co.ohmygoods.auth.account.service;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.service.dto.OAuth2AuthorizationResponse;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.auth.jwt.service.JWTService;
import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;
import co.ohmygoods.auth.oauth2.service.OAuth2AuthorizationService;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountManagementService {

    private final List<OAuth2AuthorizationService> oAuth2AuthorizationServices;
    private final JWTService jwtService;
    private final AccountRepository accountRepository;


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

    private OAuth2AuthorizationService findSupportOAuth2AuthorizationService(OAuth2Provider oAuth2Provider) {
        return oAuth2AuthorizationServices.stream()
                .filter(service -> service.canSupport(oAuth2Provider))
                .findFirst()
                .orElseThrow();
    }

}
