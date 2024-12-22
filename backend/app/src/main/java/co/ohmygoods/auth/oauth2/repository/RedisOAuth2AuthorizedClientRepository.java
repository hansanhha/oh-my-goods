package co.ohmygoods.auth.oauth2.repository;

import co.ohmygoods.auth.oauth2.model.entity.RedisOAuth2AuthorizedClient;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RedisOAuth2AuthorizedClientRepository extends CrudRepository<RedisOAuth2AuthorizedClient, String> {

    Optional<RedisOAuth2AuthorizedClient> findByEmailAndClientRegistrationId(String email, String clientRegistrationId);
}
