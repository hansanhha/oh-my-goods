package co.ohmygoods.product.repository.expression;

import co.ohmygoods.product.model.entity.QProduct;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import com.querydsl.core.types.dsl.BooleanExpression;

public class ProductCondition {

    public static BooleanExpression isEqualStatus(QProduct product, ProductStockStatus stockStatus) {
        return product.stockStatus.eq(stockStatus);
    }
}
