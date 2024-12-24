package co.ohmygoods.auth.jwt.repository;

import co.ohmygoods.auth.jwt.model.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    void removeAllByMemberId(String memberId);

    Optional<RefreshToken> findByMemberId(String memberId);
}
