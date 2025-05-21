package co.ohmygoods.shop.repository;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.shop.model.entity.Shop;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ShopRepository extends CrudRepository<Shop, Long> {

    Optional<Shop> findByName(String name);

    Optional<Shop> findByAdminMemberId(String adminMemberId);

    boolean existsByName(String name);

    boolean existsByAdmin(Account owner);

}
