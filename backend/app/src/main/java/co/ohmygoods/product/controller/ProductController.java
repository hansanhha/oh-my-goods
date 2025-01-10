package co.ohmygoods.product.controller;

import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.service.ProductService;
import co.ohmygoods.product.service.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/products")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<?> getProducts(@RequestParam(required = false) String mainCategory,
                                         @RequestParam(required = false) String subCategory,
                                         @RequestParam(required = false, defaultValue = "0") int page,
                                         @RequestParam(required = false, defaultValue = "20") int size) {

        List<ProductResponse> products = productService.getProductsByCategory(

                mainCategory != null ? ProductMainCategory.valueOf(mainCategory.toUpperCase()) : null,
                subCategory != null ? ProductSubCategory.valueOf(subCategory.toUpperCase()) : null,
                Pageable.ofSize(size).withPage(page)

        );

        return ResponseEntity.ok(products);
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<?> getProductsByShop(@PathVariable Long shopId,
                                               @RequestParam(required = false) String mainCategory,
                                               @RequestParam(required = false) String subCategory,
                                               @RequestParam(required = false) Long customCategoryId,
                                               @RequestParam(required = false, defaultValue = "0") int page,
                                               @RequestParam(required = false, defaultValue = "20") int size) {

        List<ProductResponse> products;
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        if (customCategoryId != null) {
            products = productService.getProductsByShopAndCustomCategory(shopId, customCategoryId, pageable);
        }

        else {
            products = productService.getProductsByShopAndCategory(

                    shopId,
                    mainCategory != null ? ProductMainCategory.valueOf(mainCategory.toUpperCase()) : null,
                    subCategory != null ? ProductSubCategory.valueOf(subCategory.toUpperCase()) : null,
                    pageable

            );
        }

        return ResponseEntity.ok(products);
    }
}
