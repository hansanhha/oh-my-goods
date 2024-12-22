package co.ohmygoods.auth.jwt.repository;

import co.ohmygoods.auth.jwt.model.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisRefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    void removeAllById(String id);

    Optional<RefreshToken> findById(String id);
}
