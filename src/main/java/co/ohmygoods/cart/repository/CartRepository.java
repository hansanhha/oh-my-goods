package co.ohmygoods.cart.repository;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.cart.model.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<Cart, Long> {

    @Query("SELECT c " +
            "FROM Cart c " +
            "JOIN c.account ON c.account.memberId = :memberId")
    Page<Cart> findAllByAccountMemberId(String memberId, Pageable pageable);

    int countAllByAccount(Account account);
}
