package co.ohmygoods.auth.oauth2.repository;

import co.ohmygoods.auth.oauth2.model.entity.SimpleOAuth2AuthorizedClient;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SimpleOAuth2AuthorizedClientRepository extends CrudRepository<SimpleOAuth2AuthorizedClient, Long> {

    Optional<SimpleOAuth2AuthorizedClient> findByMemberId(String memberId);

    void deleteByMemberId(String memberId);
}
