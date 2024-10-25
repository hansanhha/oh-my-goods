package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.jwt.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    List<RefreshToken> findAllBySubject(String subject);

    Optional<RefreshToken> findByJwtId(String jwtId);
}
