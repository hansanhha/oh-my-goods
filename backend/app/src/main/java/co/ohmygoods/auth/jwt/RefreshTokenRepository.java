package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.jwt.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    List<RefreshToken> findAllBySubject(String subject);
}
