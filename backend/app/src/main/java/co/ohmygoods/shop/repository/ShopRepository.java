package co.ohmygoods.shop.repository;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.shop.model.entity.Shop;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends CrudRepository<Shop, Long> {

    Optional<Shop> findByName(String name);

    Optional<Shop> findByOwnerMemberId(String ownerMemberId);

    boolean existsByName(String name);

    boolean existsByOwner(Account owner);

    String owner(Account owner);
}
