package co.ohmygoods.product.service;

import co.ohmygoods.product.service.dto.ProductDetailCategoryDto;
import co.ohmygoods.product.service.dto.ProductSeriesDto;
import co.ohmygoods.product.service.dto.ProductsByCategoryResponse;
import co.ohmygoods.product.service.dto.ProductsBySeriesResponse;
import co.ohmygoods.product.exception.DetailCategoryNotFoundException;
import co.ohmygoods.product.exception.ProductSeriesNotFoundException;
import co.ohmygoods.product.repository.ProductDetailCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.repository.ProductSeriesRepository;
import co.ohmygoods.product.model.vo.ProductTopCategory;
import co.ohmygoods.shop.exception.ShopNotFoundException;
import co.ohmygoods.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductSearchService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductDetailCategoryRepository detailCategoryRepository;
    private final ProductSeriesRepository seriesRepository;

    public ProductsByCategoryResponse getProductsByTopCategory(Long shopId, ProductTopCategory topCategory, Pageable pageable) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        var products = productRepository.findAllByShopAndTopCategory(shop, topCategory, pageable);

        var detailCategories= detailCategoryRepository
                .findAllByShopAndTopCategoryName(shop, topCategory.name())
                .stream()
                .map(detailCategory -> new ProductDetailCategoryDto(
                        topCategory.name(),
                        detailCategory.getId(),
                        detailCategory.getCategoryName()))
                .toList();

        return new ProductsByCategoryResponse(shopId,
                topCategory.name(),
                detailCategories,
                products);
    }

    public ProductsByCategoryResponse getProductsByDetailCategory(Long shopId, Long detailCategoryId, Pageable pageable) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        var detailCategory = detailCategoryRepository.findById(detailCategoryId)
                .orElseThrow(() -> new DetailCategoryNotFoundException());

        var products = productRepository.findAllByShopAndDetailCategory(shop, detailCategory, pageable);

        return new ProductsByCategoryResponse(shopId,
                detailCategory.getTopCategory().name(),
                Collections.singletonList(new ProductDetailCategoryDto(detailCategory.getTopCategory().name(), detailCategoryId, detailCategory.getCategoryName())),
                products);
    }

    public ProductsBySeriesResponse getProductsBySeries(Long shopId, Long seriesId, Pageable pageable) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        var series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new ProductSeriesNotFoundException());

        var products = productRepository.findAllByShopAndSeries(shop, series, pageable);

        return new ProductsBySeriesResponse(shopId,
                new ProductSeriesDto(seriesId, series.getSeriesName()),
                products);
    }
}
