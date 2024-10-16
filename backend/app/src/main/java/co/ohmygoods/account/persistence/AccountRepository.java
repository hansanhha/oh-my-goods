package co.ohmygoods.account.persistence;

import co.ohmygoods.account.domain.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
}
