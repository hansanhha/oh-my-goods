package co.ohmygoods.product.controller.user;

import co.ohmygoods.global.swagger.PaginationOpenAPI;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.service.user.ProductService;
import co.ohmygoods.product.service.user.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품", description = "상품 관련 api")
@RequestMapping("/api/products")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "판매 상품 목록 조회", description = "전체 상점을 대상으로 판매 중인 상품 중 카테고리 조건(쿼리 스트링)을 이용하여 상품 목록을 조회합니다")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "상품 목록 반환")
    )
    @GetMapping
    public ResponseEntity<?> getProducts(@Parameter(name = "상품 주요 카테고리 조건 지정", description = "지정된 주요 카테고리를 가진 상품 목록을 조회합니다", in = ParameterIn.QUERY)
                                         @RequestParam(required = false) String mainCategory,
                                         @Parameter(name = "상품 서브 카테고리 조건 지정", description = "지정된 서브 카테고리를 가진 상품 목록을 조회합니다. 주요 카테고리와 함께 지정한 경우 모두 만족한 상품만 조회합니다", in = ParameterIn.QUERY)
                                         @RequestParam(required = false) String subCategory,
                                         @PaginationOpenAPI.PageDescription @RequestParam(required = false, defaultValue = "0") int page,
                                         @PaginationOpenAPI.SizeDescription @RequestParam(required = false, defaultValue = "20") int size) {

        Slice<ProductResponse> products = productService.getProductsByCategory(

                mainCategory != null ? ProductMainCategory.valueOf(mainCategory.toUpperCase()) : null,
                subCategory != null ? ProductSubCategory.valueOf(subCategory.toUpperCase()) : null,
                Pageable.ofSize(size).withPage(page)

        );

        return ResponseEntity.ok(products);
    }


    @Operation(summary = "특정 상점의 판매 상품 목록 조회", description = "특정 상점을 대상으로 판매 중인 상품 중 카테고리 조건(쿼리 스트링)을 이용하여 상품 목록을 조회합니다")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "상품 목록 반환")
    )
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<?> getProductsByShop(@Parameter(name = "조회 대상 상점", in = ParameterIn.PATH) @PathVariable Long shopId,
                                               @Parameter(name = "상품 주요 카테고리 조건 지정", description = "해당 상점의 판매 상품 중 지정된 주요 카테고리를 가진 상품 목록을 조회합니다", in = ParameterIn.QUERY)
                                               @RequestParam(required = false) String mainCategory,
                                               @Parameter(name = "상품 서브 카테고리 조건 지정", description = "해당 상점의 판매 상품 중 지정된 서브 카테고리를 가진 상품 목록을 조회합니다. 주요 카테고리와 함께 지정한 경우 모두 만족한 상품만 조회합니다", in = ParameterIn.QUERY)
                                               @RequestParam(required = false) String subCategory,
                                               @Parameter(name = "상품 커스텀 카테고리 조건 지정(커스텀 카테고리 아이디)", description = "해당 상점의 판매 상품 중 지정된 커스텀 카테고리를 가진 상품 목록을 조회합니다. 주요 카테고리, 서브 카테고리와 함께 지정한 경우 모두 만족한 상품만 조회합니다", in = ParameterIn.QUERY)
                                               @RequestParam(required = false) Long customCategoryId,
                                               @PaginationOpenAPI.PageDescription @RequestParam(required = false, defaultValue = "0") int page,
                                               @PaginationOpenAPI.SizeDescription @RequestParam(required = false, defaultValue = "20") int size) {

        Slice<ProductResponse> products;
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
