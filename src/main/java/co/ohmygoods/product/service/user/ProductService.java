package co.ohmygoods.product.service.user;


import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.model.entity.CustomCategory;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductCustomCategory;
import co.ohmygoods.product.model.entity.ProductGeneralCategory;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.repository.ProductCustomCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.repository.dto.ProductShopDto;
import co.ohmygoods.product.service.user.dto.ProductCustomCategoryResponse;
import co.ohmygoods.product.service.user.dto.ProductResponse;
import co.ohmygoods.shop.exception.ShopException;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.repository.ShopRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductCustomCategoryRepository customCategoryRepository;

    public Slice<ProductResponse> getProductsByCategory(ProductMainCategory mainCategory, ProductSubCategory subCategory, Pageable pageable) {
        Slice<ProductShopDto> productShopDtos = productRepository
                .fetchAllSalesProductByGeneralCategory(ProductGeneralCategory.of(mainCategory, subCategory), pageable);

        return productShopDtos.map(dto -> convertProductResponse(dto.shopId(), dto.shopName(), dto.product()));
    }

    public Slice<ProductResponse> getProductsByShopAndCategory(Long shopId, ProductMainCategory mainCategory, ProductSubCategory subCategory, Pageable pageable) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(ShopException::notFoundShop);

        Slice<Product> products = productRepository.fetchAllSalesProductByShopAndCategory(shop, ProductGeneralCategory.of(mainCategory, subCategory), pageable);

        return products.map(product -> convertProductResponse(shop.getId(), shop.getName(), product));
    }

    public Slice<ProductResponse> getProductsByShopAndCustomCategory(Long shopId, Long customCategoryId, Pageable pageable) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(ShopException::notFoundShop);

        CustomCategory customCategory = customCategoryRepository.findById(customCategoryId)
                .orElseThrow(ProductException::notFoundCategory);

        Slice<Product> products = productRepository.fetchAllSalesProductByShopAndCustomCategory(shop, customCategory, pageable);

        return products.map(product -> convertProductResponse(shop.getId(), shop.getName(), product));
    }

    private ProductResponse convertProductResponse(Long shopId, String shopName, Product product) {
        List<ProductCustomCategoryResponse> customCategoryResponses = product.getCustomCategories()
                .stream()
                .map(ProductCustomCategory::getCustomCategory)
                .map(ProductCustomCategoryResponse::from)
                .toList();

        return ProductResponse.of(shopId, shopName, product, customCategoryResponses);
    }
}
