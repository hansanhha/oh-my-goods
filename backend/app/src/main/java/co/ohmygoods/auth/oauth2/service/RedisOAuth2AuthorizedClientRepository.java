package co.ohmygoods.auth.oauth2.service;

import co.ohmygoods.auth.oauth2.model.entity.OAuth2AuthorizedClientEntity;
import org.springframework.data.repository.CrudRepository;

public interface RedisOAuth2AuthorizedClientRepository extends CrudRepository<OAuth2AuthorizedClientEntity, Long> {
}
