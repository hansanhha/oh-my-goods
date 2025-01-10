package co.ohmygoods.product.model.entity;

import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductGeneralCategory {

    @Enumerated(EnumType.STRING)
    private ProductMainCategory mainCategory;

    @Enumerated(EnumType.STRING)
    private ProductSubCategory subCategory;

    public static ProductGeneralCategory of(ProductMainCategory mainCategory, ProductSubCategory subCategory) {
        if (mainCategory == null && subCategory == null) {
            throw ProductException.INVALID_METADATA;
        }

        if (subCategory != null) {
            mainCategory = mainCategory != null ? mainCategory : subCategory.getParentCategory();
            if (!mainCategory.contains(subCategory)) {
                throw ProductException.INVALID_METADATA;
            }
        }

        return new ProductGeneralCategory(mainCategory, subCategory);
    }

    public static ProductGeneralCategory of(ProductMainCategory mainCategory) {
        return of(mainCategory, null);
    }

    public static ProductGeneralCategory of(ProductSubCategory subCategory) {
        return of(subCategory.getParentCategory(), subCategory);
    }
}
