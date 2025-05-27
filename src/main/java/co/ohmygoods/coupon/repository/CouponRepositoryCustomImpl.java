package co.ohmygoods.coupon.repository;


import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.QShopCouponHistory;
import co.ohmygoods.coupon.model.entity.ShopCouponHistory;
import co.ohmygoods.coupon.model.entity.QCoupon;
import co.ohmygoods.coupon.repository.expression.CouponOrder;
import co.ohmygoods.coupon.repository.util.CouponRepositoryUtils;
import co.ohmygoods.shop.model.entity.Shop;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class CouponRepositoryCustomImpl implements CouponRepositoryCustom {

    private final JPAQueryFactory query;
    private final QCoupon coupon = QCoupon.coupon;
    private final QShopCouponHistory shopCouponHistory = QShopCouponHistory.shopCouponHistory;

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

    private JPQLQuery<ShopCouponHistory> filterShopSubQuery(Shop shop) {
        return JPAExpressions.select(shopCouponHistory).where(shopCouponHistory.shop.id.eq(shop.getId()));
    }
}
