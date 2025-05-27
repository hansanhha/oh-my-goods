package co.ohmygoods.coupon.repository;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.coupon.model.entity.*;
import co.ohmygoods.coupon.model.vo.CouponUsableScope;
import co.ohmygoods.coupon.model.vo.CouponUsingStatus;
import co.ohmygoods.coupon.repository.expression.CouponCondition;
import co.ohmygoods.coupon.repository.util.CouponRepositoryUtils;
import co.ohmygoods.product.model.entity.Product;

import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import static co.ohmygoods.coupon.model.vo.CouponUsableScope.*;


@Repository
@RequiredArgsConstructor
public class CouponUsingHistoryRepositoryCustomImpl implements CouponUsingHistoryRepositoryCustom {

    private final JPAQueryFactory query;
    private final QCouponUsingHistory cuh = QCouponUsingHistory.couponUsingHistory;
    private final QCouponUsableProduct cup = QCouponUsableProduct.couponUsableProduct;


    @Override
    public Optional<CouponUsingHistory> findByAccountAndCouponAndStatus(Account account, Coupon coupon, CouponUsingStatus status) {

        CouponUsingHistory result = query
                .selectFrom(cuh)
                .leftJoin(cuh.coupon).on(cuh.coupon.id.eq(coupon.getId()))
                .leftJoin(cuh.account).on(cuh.account.email.eq(account.getEmail()))
                .where(CouponCondition.isEqualStatus(cuh, status))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Slice<CouponUsingHistory> fetchAllByAccountAndStatus(Account account, CouponUsingStatus status, Pageable pageable) {

        List<CouponUsingHistory> results = query
                .selectFrom(cuh)
                .leftJoin(cuh.coupon).fetchJoin()
                .join(cuh.account).on(cuh.account.memberId.eq(account.getMemberId()))
                .where(CouponCondition.isEqualStatus(cuh, status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return CouponRepositoryUtils.createSlicePaginationResult(results, pageable);
    }

    @Override
    public List<CouponUsingHistory> fetchAllUsableByProduct(Account account, Product product) {

        EnumPath<CouponUsableScope> usableScope = cuh.coupon.usableScope;

        return query
                .selectFrom(cuh)
                .join(cuh.account).on(cuh.account.memberId.eq(account.getMemberId()))
                .where(usableScope.eq(PLATFORM_ALL_PRODUCT)
                        .or(usableScope.eq(SHOP_ALL_PRODUCT).and(cuh.coupon.shop.eq(product.getShop())))
                        .or(usableScope.eq(PLATFORM_SPECIFIC_PRODUCTS).or(usableScope.eq(SHOP_SPECIFIC_PRODUCTS)).and(cuh.coupon.id.in(
                                JPAExpressions
                                        .select(cup.coupon.id)
                                        .from(cup)
                                        .where(cup.product.id.eq(product.getId()))
                                ))
                        )
                )
                .fetch();
    }

}
