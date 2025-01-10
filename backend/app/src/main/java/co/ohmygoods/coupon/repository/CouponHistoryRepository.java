package co.ohmygoods.coupon.repository;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CouponHistoryRepository extends
        CrudRepository<CouponHistory, Long>, CouponHistoryCustomRepositoryCustom {

    List<CouponHistory> findAllByCouponAndAccount(Coupon coupon, Account account);

}
