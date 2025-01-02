package co.ohmygoods.product.controller;

import co.ohmygoods.product.model.vo.ProductMainCategory;
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
    public ResponseEntity<?> getProducts(@RequestParam(defaultValue = "GAME") String mainCategory,
                                         @RequestParam(required = false, defaultValue = "0") int page,
                                         @RequestParam(required = false, defaultValue = "20") int size) {

        List<ProductResponse> products = productService.getProductsByMainCategory(ProductMainCategory.valueOf(mainCategory.toUpperCase()), Pageable.ofSize(size).withPage(page));
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

        if (StringUtils.hasText(mainCategory)) {
            products = productService.getProductsByShopAndMainCategory(shopId, ProductMainCategory.valueOf(mainCategory.toUpperCase()), Pageable.ofSize(size).withPage(page));
        }
        else if (StringUtils.hasText(subCategory)) {
            products = productService.getProductsByShopAndSubCategory(shopId, subCategory, Pageable.ofSize(size).withPage(page));
        }
        else if (customCategoryId != null) {
            products = productService.getProductsByShopAndCustomCategory(shopId, customCategoryId, Pageable.ofSize(size).withPage(page));
        }
        else {
            products = productService.getProductsByShop(shopId, Pageable.ofSize(size).withPage(page));
        }

        return ResponseEntity.ok(products);
    }
}
