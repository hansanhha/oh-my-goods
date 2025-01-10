package co.ohmygoods.product.repository;

import co.ohmygoods.product.model.entity.*;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.repository.dto.ProductShopDto;
import co.ohmygoods.shop.model.entity.Shop;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static co.ohmygoods.product.repository.expression.ProductCondition.isEqualStatus;
import static co.ohmygoods.product.repository.expression.ProductOrderSpecifiers.sortByCreatedAtDesc;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory query;
    private final QProduct product = QProduct.product;

    @Override
    public Slice<ProductShopDto> fetchAllSalesProductByGeneralCategory(ProductGeneralCategory category, Pageable pageable) {

        List<ProductShopDto> results = query
                .select(Projections.constructor(ProductShopDto.class, product.shop.id, product.shop.name, product))
                .from(product)
                .leftJoin(product.shop).fetchJoin()
                .leftJoin(product.customCategories).fetchJoin()
                .where(buildCategoryCondition(category).and(isEqualStatus(product, ProductStockStatus.ON_SALES)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(sortByCreatedAtDesc(product))
                .fetch();

        return createPaginationResult(results, pageable);
    }

    @Override
    public Slice<Product> fetchAllSalesProductByShopAndCategory(Shop shop, ProductGeneralCategory category, Pageable pageable) {

        List<Product> results = query
                .selectFrom(product)
                .leftJoin(product.shop).on(product.shop.eq(shop))
                .leftJoin(product.customCategories).fetchJoin()
                .where(buildCategoryCondition(category).and(isEqualStatus(product, ProductStockStatus.ON_SALES)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(sortByCreatedAtDesc(product))
                .fetch();

        return createPaginationResult(results, pageable);
    }

    @Override
    public Slice<Product> fetchAllSalesProductByShopAndCustomCategory(Shop shop, ProductCustomCategory category, Pageable pageable) {
        if (category == null) {
            return fetchAllSalesProductByShopAndCategory(shop, null, pageable);
        }

        QProductCustomCategoryMapping ccm = QProductCustomCategoryMapping.productCustomCategoryMapping;

        JPQLQuery<ProductCustomCategoryMapping> filterCustomCategoryMapping = JPAExpressions
                .selectFrom(ccm)
                .where(ccm.customCategory.id.eq(category.getId())
                        .and(ccm.customCategory.shop.id.eq(shop.getId())));

        List<Product> results = query
                .selectFrom(product)
                .leftJoin(product.customCategories)
                .on(product.customCategories.contains(filterCustomCategoryMapping))
                .fetchJoin()
                .where(isEqualStatus(product, ProductStockStatus.ON_SALES))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(sortByCreatedAtDesc(product))
                .fetch();

        return createPaginationResult(results, pageable);
    }

    private static <T> Slice<T> createPaginationResult(List<T> results, Pageable pageable) {
        boolean hasNext = results.size() > pageable.getPageSize();

        if (!results.isEmpty() && hasNext) {
            results.removeLast();
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    private BooleanExpression buildCategoryCondition(ProductGeneralCategory category) {
        if (category == null || (category.getMainCategory() == null && category.getSubCategory() == null)) {
            return Expressions.TRUE;
        }

        BooleanExpression condition = null;

        ProductMainCategory mainCategory = category.getMainCategory();
        ProductSubCategory subCategory = category.getSubCategory();

        if (mainCategory != null) {
            condition = product.category.mainCategory.eq(mainCategory);
        }

        if (subCategory != null) {
            if (mainCategory != null && !mainCategory.contains(subCategory)) {
                return condition;
            }

            condition = (condition == null)
                    ? product.category.subCategory.eq(subCategory)
                    : condition.and(product.category.subCategory.eq(subCategory));
        }

        return condition;
    }
}
