package co.ohmygoods.order.repository;

import co.ohmygoods.order.model.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeliveryAddressRepository extends CrudRepository<DeliveryAddress, Long> {

    @Query("SELECT da FROM DeliveryAddress da WHERE da.account.email = :accountEmail")
    List<DeliveryAddress> findAllByAccountEmail(String accountEmail);
}
