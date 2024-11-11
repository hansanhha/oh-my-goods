package co.ohmygoods.cart.repository;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.cart.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<Cart, Long> {

    Page<Cart> findAllByAccount(OAuth2Account account, Pageable pageable);

    int countAllByAccount(OAuth2Account account);
}
