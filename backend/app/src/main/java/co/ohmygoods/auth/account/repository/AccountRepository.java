package co.ohmygoods.auth.account.repository;

import co.ohmygoods.auth.account.model.entity.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByOauth2MemberId(String oauth2MemberId);

    Optional<Account> findByMemberId(String memberId);
}
