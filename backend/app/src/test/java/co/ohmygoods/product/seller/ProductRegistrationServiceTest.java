package co.ohmygoods.product.seller;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.product.entity.*;
import co.ohmygoods.product.exception.InvalidProductDetailCategoryException;
import co.ohmygoods.product.exception.InvalidProductSeriesException;
import co.ohmygoods.product.repository.ProductDetailCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.repository.ProductSeriesRepository;
import co.ohmygoods.seller.product.service.ProductRegistrationService;
import co.ohmygoods.seller.product.dto.ProductMetadataModifyInfo;
import co.ohmygoods.seller.product.dto.ProductRegisterRequest;
import co.ohmygoods.product.vo.ProductStockStatus;
import co.ohmygoods.product.vo.ProductTopCategory;
import co.ohmygoods.product.vo.ProductType;
import co.ohmygoods.shop.entity.Shop;
import co.ohmygoods.shop.exception.InvalidShopOwnerException;
import co.ohmygoods.shop.repository.ShopRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ProductRegistrationServiceTest {

    @Mock
    private AccountRepository mockAccountRepository;

    @Mock
    private ShopRepository mockShopRepository;

    @Mock
    private ProductRepository mockProductRepository;

    @Mock
    private ProductDetailCategoryRepository mockProductDetailCategoryRepository;

    @Mock
    private ProductSeriesRepository mockProductSeriesRepository;

    @InjectMocks
    private ProductRegistrationService productRegistrationService;

    @Mock
    private Product mockProduct;

    @Captor
    ArgumentCaptor<Product> productArgumentCaptor;

    @Captor
    ArgumentCaptor<List<ProductDetailCategoryMapping>> detailCategoryMappingsCaptor;

    @Captor
    ArgumentCaptor<List<ProductSeriesMapping>> seriesMappingsCaptor;

    @Mock
    private ProductDetailCategory mockProductDetailCategory;

    @Mock
    private ProductSeries mockProductSeries;

    @Mock
    private Shop mockShop;

    @Mock
    private OAuth2Account mockAccount;

    private static final Long SHOP_ID = 1L;
    private static final String ACCOUNT_EMAIL = "test@test.com";

    @Test
    void 상품_상세_카테고리_등록() {
        var topCategory = ProductTopCategory.GAME;
        var detailCategoryName = "newCategory";

        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
        given(mockProductDetailCategoryRepository.findByCategoryName(anyString())).willReturn(Optional.empty());
        given(mockProductDetailCategoryRepository.save(any(ProductDetailCategory.class))).willReturn(mockProductDetailCategory);
        given(mockProductDetailCategory.getId()).willReturn(1L);
        given(mockProductDetailCategory.getCategoryName()).willReturn(detailCategoryName);

        var response = productRegistrationService.registerProductDetailCategory(SHOP_ID, ACCOUNT_EMAIL, topCategory, detailCategoryName);

        then(mockShopRepository).should(times(1)).findById(anyLong());
        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
        then(mockProductDetailCategoryRepository).should(times(1)).findByCategoryName(anyString());
        then(mockProductDetailCategoryRepository).should(times(1)).save(any(ProductDetailCategory.class));

        assertThat(response.categoryName()).isEqualTo(detailCategoryName);
        assertThat(response.topCategoryName()).isEqualTo(topCategory.name());
    }

    @Test
    void 상품_시리즈_등록() {
        var seriesName = "newSeries";

        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
        given(mockProductSeriesRepository.findBySeriesName(anyString())).willReturn(Optional.empty());
        given(mockProductSeriesRepository.save(any(ProductSeries.class))).willReturn(mockProductSeries);
        given(mockProductSeries.getId()).willReturn(1L);
        given(mockProductSeries.getSeriesName()).willReturn(seriesName);

        var response = productRegistrationService.registerProductSeries(SHOP_ID, ACCOUNT_EMAIL, seriesName);

        then(mockShopRepository).should(times(1)).findById(anyLong());
        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
        then(mockProductSeriesRepository).should(times(1)).findBySeriesName(anyString());
        then(mockProductSeriesRepository).should(times(1)).save(any(ProductSeries.class));

        assertThat(response.seriesName()).isEqualTo(seriesName);
    }

    @Test
    void 중복된_상세_카테고리_이름은_등록할수없음() {
        var topCategory = ProductTopCategory.GAME;
        var duplicateDetailCategoryName = "duplicate";

        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
        given(mockProductDetailCategoryRepository.findByCategoryName(anyString())).willReturn(Optional.of(mockProductDetailCategory));

        assertThatThrownBy(() -> productRegistrationService.registerProductDetailCategory(SHOP_ID, ACCOUNT_EMAIL, topCategory, duplicateDetailCategoryName))
                .isExactlyInstanceOf(InvalidProductDetailCategoryException.class);

        then(mockShopRepository).should(times(1)).findById(anyLong());
        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
        then(mockProductDetailCategoryRepository).should(times(1)).findByCategoryName(anyString());
        then(mockProductDetailCategoryRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void 중복된_시리즈_이름은_등록할수없음() {
        var duplicateSeriesName = "duplicate";

        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
        given(mockProductSeriesRepository.findBySeriesName(anyString())).willReturn(Optional.of(mockProductSeries));

        assertThatThrownBy(() -> productRegistrationService.registerProductSeries(SHOP_ID, ACCOUNT_EMAIL, duplicateSeriesName))
                .isExactlyInstanceOf(InvalidProductSeriesException.class);

        then(mockShopRepository).should(times(1)).findById(anyLong());
        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
        then(mockProductSeriesRepository).should(times(1)).findBySeriesName(anyString());
        then(mockProductSeriesRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void 상세_카테고리와_시리즈_할인_없이_즉시_판매_상품_등록() {
        var tempProductId = 1L;
        var name = "test product";
        var description = "test description";
        var topCategory = ProductTopCategory.GAME;
        var price = 10000;
        var quantity = 100;
        var stockStatus = ProductStockStatus.ON_SALES;
        var type = ProductType.ANALOGUE;

        var productRegisterRequest = ProductRegisterRequest.builder()
                .shopId(SHOP_ID)
                .accountEmail(ACCOUNT_EMAIL)
                .name(name)
                .category(topCategory)
                .description(description)
                .price(price)
                .quantity(quantity)
                .status(stockStatus)
                .type(type)
                .build();

        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
        given(mockProductRepository.save(productArgumentCaptor.capture())).willReturn(mockProduct);
        given(mockProduct.getId()).willReturn(tempProductId);

        var response = productRegistrationService.registerProduct(productRegisterRequest);

        then(mockShopRepository).should(times(1)).findById(anyLong());
        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
        then(mockProductRepository).should(times(1)).save(any(Product.class));
        then(mockProductDetailCategoryRepository).shouldHaveNoInteractions();
        then(mockProductSeriesRepository).shouldHaveNoInteractions();

        var product = productArgumentCaptor.getValue();

        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getStockStatus()).isEqualTo(stockStatus);
        assertThat(product.getType()).isEqualTo(type);
        assertThat(product.getRemainingQuantity()).isEqualTo(quantity);
        assertThat(product.getOriginalPrice()).isEqualTo(price);
        assertThat(product.getShop()).isEqualTo(mockShop);
        assertThat(product.getSaleStartDate()).isNotNull();
        assertThat(product.getSaleEndDate()).isNull();
        assertThat(product.getSeriesMappings()).isNull();
        assertThat(product.getDetailCategoryMappings()).isNull();

        assertThat(response).isEqualTo(tempProductId);
    }

    @Test
    void 상세_카테고리와_시리즈와_함께_상품_등록() {
        var tempProductId = 1L;
        var name = "test product";
        var description = "test description";
        var topCategory = ProductTopCategory.GAME;
        var price = 10000;
        var quantity = 100;
        var stockStatus = ProductStockStatus.TO_BE_SOLD;
        var type = ProductType.ANALOGUE;
        var isImmediateSale = false;
        var detailCategoryIds = List.of(1L, 2L, 3L);
        var seriesIds = List.of(1L);

        var mockProductDetailCategories = detailCategoryIds.stream()
                .map(d -> mock(ProductDetailCategory.class))
                .toList();
        var mockProductSeries = detailCategoryIds.stream()
                .map(d -> mock(ProductSeries.class))
                .toList();

        var productRegisterRequest = ProductRegisterRequest.builder()
                .shopId(SHOP_ID)
                .accountEmail(ACCOUNT_EMAIL)
                .name(name)
                .category(topCategory)
                .description(description)
                .price(price)
                .quantity(quantity)
                .status(stockStatus)
                .type(type)
                .detailCategoryIds(detailCategoryIds)
                .seriesIds(seriesIds)
                .build();

        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
        given(mockProductRepository.save(any(Product.class))).willReturn(mockProduct);
        given(mockProduct.getId()).willReturn(tempProductId);
        given(mockProductDetailCategoryRepository.findAllByIdAndShop(anyIterable(), any(Shop.class))).willReturn(mockProductDetailCategories);
        given(mockProductSeriesRepository.findAllByIdAndShop(anyIterable(), any(Shop.class))).willReturn(mockProductSeries);

        var response = productRegistrationService.registerProduct(productRegisterRequest);

        then(mockShopRepository).should(times(1)).findById(anyLong());
        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
        then(mockProductRepository).should(times(1)).save(any(Product.class));
        then(mockProductDetailCategoryRepository).should(times(1)).findAllByIdAndShop(anyIterable(), any(Shop.class));
        then(mockProductSeriesRepository).should(times(1)).findAllByIdAndShop(anyIterable(), any(Shop.class));

        verify(mockProductRepository).save(productArgumentCaptor.capture());
        verify(mockProduct).setDetailCategoryMappings(detailCategoryMappingsCaptor.capture());
        verify(mockProduct).setSeriesMappings(seriesMappingsCaptor.capture());

        var product = productArgumentCaptor.getValue();
        var detailCategoryMappings = detailCategoryMappingsCaptor.getValue();
        var seriesMappings = seriesMappingsCaptor.getValue();

        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getStockStatus()).isEqualTo(stockStatus);
        assertThat(product.getType()).isEqualTo(type);
        assertThat(product.getRemainingQuantity()).isEqualTo(quantity);
        assertThat(product.getOriginalPrice()).isEqualTo(price);
        assertThat(product.getShop()).isEqualTo(mockShop);
        assertThat(product.getSaleStartDate()).isNull();
        assertThat(product.getSaleEndDate()).isNull();

        assertThat(detailCategoryMappings.size()).isEqualTo(mockProductDetailCategories.size());
        assertThat(seriesMappings.size()).isEqualTo(mockProductSeries.size());

        assertThat(response).isEqualTo(tempProductId);
    }

    @Test
    void 할인__판매_예정_상품_등록() {
        var tempProductId = 1L;
        var name = "test product";
        var description = "test description";
        var topCategory = ProductTopCategory.GAME;
        var price = 10000;
        var quantity = 100;
        var stockStatus = ProductStockStatus.TO_BE_SOLD;
        var type = ProductType.ANALOGUE;
        var saleStartDate = LocalDateTime.now().plusDays(7);
        var discountRate = 20;
        var discountStartDate = LocalDateTime.now().plusDays(7);
        var discountEndDate = LocalDateTime.now().plusDays(14);

        var productRegisterRequest = ProductRegisterRequest.builder()
                .shopId(SHOP_ID)
                .accountEmail(ACCOUNT_EMAIL)
                .name(name)
                .category(topCategory)
                .description(description)
                .price(price)
                .quantity(quantity)
                .status(stockStatus)
                .type(type)
                .expectedSaleDate(saleStartDate)
                .discountRate(discountRate)
                .discountStartDate(discountStartDate)
                .discountEndDate(discountEndDate)
                .build();

        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
        given(mockProductRepository.save(productArgumentCaptor.capture())).willReturn(mockProduct);
        given(mockProduct.getId()).willReturn(tempProductId);

        var response = productRegistrationService.registerProduct(productRegisterRequest);

        then(mockShopRepository).should(times(1)).findById(anyLong());
        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
        then(mockProductRepository).should(times(1)).save(any(Product.class));
        then(mockProductDetailCategoryRepository).shouldHaveNoInteractions();
        then(mockProductSeriesRepository).shouldHaveNoInteractions();

        var product = productArgumentCaptor.getValue();

        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getStockStatus()).isEqualTo(stockStatus);
        assertThat(product.getType()).isEqualTo(type);
        assertThat(product.getRemainingQuantity()).isEqualTo(quantity);
        assertThat(product.getOriginalPrice()).isEqualTo(price);
        assertThat(product.getShop()).isEqualTo(mockShop);
        assertThat(product.getSeriesMappings()).isNull();
        assertThat(product.getDetailCategoryMappings()).isNull();
        assertThat(product.getSaleStartDate()).isNotNull();
        assertThat(product.getSaleEndDate()).isNull();
        assertThat(product.getDiscountRate()).isEqualTo(discountRate);
        assertThat(product.getDiscountStartDate()).isEqualTo(discountStartDate);
        assertThat(product.getDiscountEndDate()).isEqualTo(discountEndDate);

        assertThat(response).isEqualTo(tempProductId);
    }

    @Test
    void 상점_주인_계정이_아니면_상품을_등록_할수없음() {
        var mockProductRegisterRequest = mock(ProductRegisterRequest.class);

        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
        given(mockProductRegisterRequest.shopId()).willReturn(SHOP_ID);
        given(mockProductRegisterRequest.accountEmail()).willReturn(ACCOUNT_EMAIL);
        doThrow(InvalidShopOwnerException.class).when(mockShop).ownerCheck(mockAccount);

        assertThatThrownBy(() -> productRegistrationService.registerProduct(mockProductRegisterRequest))
                .isExactlyInstanceOf(InvalidShopOwnerException.class);

        then(mockShopRepository).should(times(1)).findById(anyLong());
        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
        then(mockProductRepository).shouldHaveNoInteractions();
        then(mockProductDetailCategoryRepository).shouldHaveNoInteractions();
        then(mockProductSeriesRepository).shouldHaveNoInteractions();
    }

    @Test
    void 등록된_상품_리스트_페이징_조회() {

    }

    @Test
    void 상품_정보_수정() {
        var modifyProductId = 1L;
        var modifyName = "test modify product";
        var modifyDescription = "test modify description";
        var modifyTopCategory = ProductTopCategory.IDOL;
        var modifyType = ProductType.ANALOGUE_EXCLUSIVE;

        var productMetadataModifyInfo = ProductMetadataModifyInfo.builder()
                .shopId(SHOP_ID)
                .modifyProductId(modifyProductId)
                .accountEmail(ACCOUNT_EMAIL)
                .modifyName(modifyName)
                .modifyDescription(modifyDescription)
                .modifyTopCategory(modifyTopCategory)
                .modifyType(modifyType)
                .build();

        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
        given(mockProductRepository.findById(anyLong())).willReturn(Optional.of(mockProduct));

        productRegistrationService.modifyProductMetadata(productMetadataModifyInfo);

        then(mockShopRepository).should(times(1)).findById(anyLong());
        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
        then(mockProductRepository).should(times(1)).findById(anyLong());
        then(mockProductDetailCategoryRepository).shouldHaveNoInteractions();
        then(mockProductSeriesRepository).shouldHaveNoInteractions();
        then(mockProduct).should(times(1)).updateMetadata(modifyName, modifyDescription, modifyType, modifyTopCategory, null, null);
    }

    @Test
    void 상품_삭제() {
        var deleteProductId = 1L;

        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
        given(mockProductRepository.findById(anyLong())).willReturn(Optional.of(mockProduct));

        productRegistrationService.delete(SHOP_ID, ACCOUNT_EMAIL, deleteProductId);

        then(mockShopRepository).should(times(1)).findById(anyLong());
        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
        then(mockProductRepository).should(times(1)).findById(anyLong());
        then(mockProductRepository).should(times(1)).delete(any(Product.class));
    }


}