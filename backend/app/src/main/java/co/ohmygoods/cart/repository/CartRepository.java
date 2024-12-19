package co.ohmygoods.cart.repository;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.cart.model.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<Cart, Long> {

    Page<Cart> findAllByAccount(Account account, Pageable pageable);

    int countAllByAccount(Account account);
}
