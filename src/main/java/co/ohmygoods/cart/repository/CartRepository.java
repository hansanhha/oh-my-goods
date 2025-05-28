package co.ohmygoods.cart.repository;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.cart.model.entity.Cart;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface CartRepository extends CrudRepository<Cart, Long> {

    @Query("""
           SELECT c
           FROM Cart c
           JOIN FETCH c.product p
           JOIN FETCH p.shop s
           WHERE c.account.memberId = :memberId
           """)
    Slice<Cart> fetchAllShopAndProductByMemberId(@Param("memberId") String memberId, Pageable pageable);

    int countAllByAccount(Account account);
}
