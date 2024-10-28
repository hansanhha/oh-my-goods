package co.ohmygoods.auth.account;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<OAuth2Account, Long> {

    Optional<OAuth2Account> findByEmail(String email);
}
