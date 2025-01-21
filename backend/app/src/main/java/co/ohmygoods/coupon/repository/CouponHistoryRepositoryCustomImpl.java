package co.ohmygoods.coupon.repository;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponHistory;
import co.ohmygoods.coupon.model.entity.QCouponHistory;
import co.ohmygoods.coupon.model.vo.CouponHistoryStatus;
import co.ohmygoods.coupon.repository.expression.CouponCondition;
import co.ohmygoods.coupon.repository.util.CouponRepositoryUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponHistoryRepositoryCustomImpl implements CouponHistoryRepositoryCustom {

    private final JPAQueryFactory query;
    private final QCouponHistory couponHistory = QCouponHistory.couponHistory;

    @Override
    public Optional<CouponHistory> fetchIssuedCouponHistoryByAccountAndCoupon(Account account, Coupon coupon) {

        CouponHistory result = query
                .selectFrom(couponHistory)
                .leftJoin(couponHistory.coupon)
                    .on(couponHistory.coupon.id.eq(coupon.getId())).fetchJoin()
                .leftJoin(couponHistory.account)
                    .on(couponHistory.account.email.eq(account.getEmail())).fetchJoin()
                .where(CouponCondition.isEqualStatus(couponHistory, CouponHistoryStatus.ISSUED))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Slice<CouponHistory> fetchAllIssuedCouponHistoryByAccount(Account account, Pageable pageable) {

        List<CouponHistory> results = query
                .selectFrom(couponHistory)
                .leftJoin(couponHistory.coupon)
                .join(couponHistory.account).on(couponHistory.account.email.eq(account.getEmail()))
                .where(CouponCondition.isEqualStatus(couponHistory, CouponHistoryStatus.ISSUED))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return CouponRepositoryUtils.createSlicePaginationResult(results, pageable);
    }

    @Override
    public List<CouponHistory> findAllByAccountEmailAndId(String accountEmail, List<Long> couponHistoryIds) {

        return query
                .selectFrom(couponHistory)
                .join(couponHistory.account).on(couponHistory.account.email.eq(accountEmail))
                .where(couponHistory.id.in(couponHistoryIds))
                .fetch();
    }


}
