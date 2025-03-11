package co.ohmygoods.product.repository.expression;

import co.ohmygoods.product.model.entity.QProduct;
import com.querydsl.core.types.OrderSpecifier;

import java.time.LocalDateTime;

public class ProductOrderSpecifiers {

    public static OrderSpecifier<LocalDateTime> sortByCreatedAtDesc(QProduct product) {
        return product.createdAt.desc();
    }
}
