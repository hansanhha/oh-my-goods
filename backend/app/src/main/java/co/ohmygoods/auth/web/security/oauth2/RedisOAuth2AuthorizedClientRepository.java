package co.ohmygoods.auth.web.security.oauth2;

import co.ohmygoods.auth.model.entity.OAuth2AuthorizedClientEntity;
import org.springframework.data.repository.CrudRepository;

public interface RedisOAuth2AuthorizedClientRepository extends CrudRepository<OAuth2AuthorizedClientEntity, Long> {
}
