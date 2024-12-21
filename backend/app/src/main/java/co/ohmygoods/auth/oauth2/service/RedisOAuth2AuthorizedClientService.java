package co.ohmygoods.auth.oauth2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RedisOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {



    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return null;
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {

    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {

    }
}
