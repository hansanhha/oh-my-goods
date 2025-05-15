package co.ohmygoods.coupon.repository;

import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponShopMapping;
import co.ohmygoods.coupon.model.entity.QCoupon;
import co.ohmygoods.coupon.model.entity.QCouponShopMapping;
import co.ohmygoods.coupon.repository.expression.CouponOrder;
import co.ohmygoods.coupon.repository.util.CouponRepositoryUtils;
import co.ohmygoods.shop.model.entity.Shop;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryCustomImpl implements CouponRepositoryCustom {

    private final JPAQueryFactory query;
    private final QCoupon coupon = QCoupon.coupon;
    private final QCouponShopMapping couponShopMapping = QCouponShopMapping.couponShopMapping;

    @Override
    public Slice<Coupon> fetchAllByShop(Shop shop, Pageable pageable) {

        List<Coupon> results = query.
                selectFrom(coupon)
                .leftJoin(coupon.issuer).fetchJoin()
                .where(filterShopSubQuery(shop).exists())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(CouponOrder.sortByCreatedAtDesc(coupon))
                .fetch();

        return CouponRepositoryUtils.createSlicePaginationResult(results, pageable);
    }

    @Override
    public Optional<Coupon> findByShopAndCouponId(Shop shop, Long couponId) {

        Coupon result = query
                .selectFrom(coupon)
                .where(coupon.id.eq(couponId).and(filterShopSubQuery(shop).exists()))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private JPQLQuery<CouponShopMapping> filterShopSubQuery(Shop shop) {
        return JPAExpressions.select(couponShopMapping).where(couponShopMapping.applyTargetShop.id.eq(shop.getId()));
    }
}
