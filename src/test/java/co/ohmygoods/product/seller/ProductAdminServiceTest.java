package co.ohmygoods.product.seller;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.product.model.entity.*;
import co.ohmygoods.product.repository.ProductCustomCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.service.admin.ProductAdminService;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.repository.ShopRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@Disabled("테스트 코드 리팩토링 필요")
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ProductAdminServiceTest {

    @Mock
    private AccountRepository mockAccountRepository;

    @Mock
    private ShopRepository mockShopRepository;

    @Mock
    private ProductRepository mockProductRepository;

    @Mock
    private ProductCustomCategoryRepository mockProductCustomCategoryRepository;

    @InjectMocks
    private ProductAdminService productAdminService;

    @Mock
    private Product mockProduct;

    @Captor
    ArgumentCaptor<Product> productArgumentCaptor;

    @Captor
    ArgumentCaptor<List<ProductCustomCategory>> detailCategoryMappingsCaptor;

    @Mock
    private CustomCategory mockCustomCategory;

    @Mock
    private Shop mockShop;

    @Mock
    private Account mockAccount;

    private static final Long SHOP_ID = 1L;
    private static final String ACCOUNT_EMAIL = "test@test.com";

    @Test
    void 상품_상세_카테고리_등록() {
//        var topCategory = ProductMainCategory.GAME;
//        var detailCategoryName = "newCategory";
//
//        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
//        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
//        given(mockProductCustomCategoryRepository.findByCustomCategoryName(anyString())).willReturn(Optional.empty());
//        given(mockProductCustomCategoryRepository.save(any(ProductCustomCategory.class))).willReturn(mockProductCustomCategory);
//        given(mockProductCustomCategory.getId()).willReturn(1L);
//        given(mockProductCustomCategory.getCustomCategoryName()).willReturn(detailCategoryName);
//
//        var response = sellerProductRegistrationService.registerCustomCategory(SHOP_ID, ACCOUNT_EMAIL, detailCategoryName);
//
//        then(mockShopRepository).should(times(1)).findById(anyLong());
//        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
//        then(mockProductCustomCategoryRepository).should(times(1)).findByCustomCategoryName(anyString());
//        then(mockProductCustomCategoryRepository).should(times(1)).save(any(ProductCustomCategory.class));
//
//        assertThat(response.customCategoryName()).isEqualTo(detailCategoryName);
    }

    @Test
    void 중복된_상세_카테고리_이름은_등록할수없음() {
//        var topCategory = ProductMainCategory.GAME;
//        var duplicateDetailCategoryName = "duplicate";
//
//        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
//        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
//        given(mockProductCustomCategoryRepository.findByCustomCategoryName(anyString())).willReturn(Optional.of(mockProductCustomCategory));
//
//        assertThatThrownBy(() -> sellerProductRegistrationService.registerCustomCategory(SHOP_ID, ACCOUNT_EMAIL, duplicateDetailCategoryName))
//                .isExactlyInstanceOf(InvalidProductCustomCategoryException.class);
//
//        then(mockShopRepository).should(times(1)).findById(anyLong());
//        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
//        then(mockProductCustomCategoryRepository).should(times(1)).findByCustomCategoryName(anyString());
//        then(mockProductCustomCategoryRepository).shouldHaveNoMoreInteractions();
    }


    @Test
    void 상세_카테고리_할인_없이_즉시_판매_상품_등록() {
//        var tempProductId = 1L;
//        var name = "test product";
//        var description = "test description";
//        var mainCategory = ProductMainCategory.GAME;
//        var price = 10000;
//        var quantity = 100;
//        var stockStatus = ProductStockStatus.ON_SALES;
//        var type = ProductType.ANALOGUE;
//
//        var productRegisterRequest = RegisterProductRequest.builder()
//                .email(ACCOUNT_EMAIL)
//                .name(name)
//                .mainCategory(mainCategory)
//                .description(description)
//                .price(price)
//                .quantity(quantity)
//                .status(stockStatus)
//                .type(type)
//                .build();
//
//        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
//        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
//        given(mockProductRepository.save(productArgumentCaptor.capture())).willReturn(mockProduct);
//        given(mockProduct.getId()).willReturn(tempProductId);
//
//        var response = sellerProductRegistrationService.registerProduct(productRegisterRequest);
//
//        then(mockShopRepository).should(times(1)).findById(anyLong());
//        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
//        then(mockProductRepository).should(times(1)).save(any(Product.class));
//        then(mockProductCustomCategoryRepository).shouldHaveNoInteractions();
//
//        var product = productArgumentCaptor.getValue();
//
//        assertThat(product.getName()).isEqualTo(name);
//        assertThat(product.getStockStatus()).isEqualTo(stockStatus);
//        assertThat(product.getType()).isEqualTo(type);
//        assertThat(product.getRemainingQuantity()).isEqualTo(quantity);
//        assertThat(product.getOriginalPrice()).isEqualTo(price);
//        assertThat(product.getShop()).isEqualTo(mockShop);
//        assertThat(product.getSaleStartDate()).isNotNull();
//        assertThat(product.getSaleEndDate()).isNull();
//        assertThat(product.getCustomCategories()).isNull();
//
//        assertThat(response).isEqualTo(tempProductId);
    }

    @Test
    void 상세_카테고리와_시리즈와_함께_상품_등록() {
//        var tempProductId = 1L;
//        var name = "test product";
//        var description = "test description";
//        var mainCategory = ProductMainCategory.GAME;
//        var price = 10000;
//        var quantity = 100;
//        var stockStatus = ProductStockStatus.TO_BE_SOLD;
//        var type = ProductType.ANALOGUE;
//        var isImmediateSale = false;
//        var customCategoryIds = List.of(1L, 2L, 3L);
//
//        var mockProductDetailCategories = customCategoryIds.stream()
//                .map(d -> mock(ProductCustomCategory.class))
//                .toList();
//
//        var productRegisterRequest = RegisterProductRequest.builder()
//                .shopId(SHOP_ID)
//                .email(ACCOUNT_EMAIL)
//                .name(name)
//                .mainCategory(mainCategory)
//                .description(description)
//                .price(price)
//                .quantity(quantity)
//                .status(stockStatus)
//                .type(type)
//                .customCategoryIds(customCategoryIds)
//                .build();
//
//        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
//        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
//        given(mockProductRepository.save(any(Product.class))).willReturn(mockProduct);
//        given(mockProduct.getId()).willReturn(tempProductId);
//        given(mockProductCustomCategoryRepository.findAllByIdAndShop(anyIterable(), any(Shop.class))).willReturn(mockProductDetailCategories);
//
//        var response = sellerProductRegistrationService.registerProduct(productRegisterRequest);
//
//        then(mockShopRepository).should(times(1)).findById(anyLong());
//        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
//        then(mockProductRepository).should(times(1)).save(any(Product.class));
//        then(mockProductCustomCategoryRepository).should(times(1)).findAllByIdAndShop(anyIterable(), any(Shop.class));
//
//        verify(mockProductRepository).save(productArgumentCaptor.capture());
//
//        var product = productArgumentCaptor.getValue();
//        var detailCategoryMappings = detailCategoryMappingsCaptor.getValue();
//
//        assertThat(product.getName()).isEqualTo(name);
//        assertThat(product.getStockStatus()).isEqualTo(stockStatus);
//        assertThat(product.getType()).isEqualTo(type);
//        assertThat(product.getRemainingQuantity()).isEqualTo(quantity);
//        assertThat(product.getOriginalPrice()).isEqualTo(price);
//        assertThat(product.getShop()).isEqualTo(mockShop);
//        assertThat(product.getSaleStartDate()).isNull();
//        assertThat(product.getSaleEndDate()).isNull();
//
//        assertThat(detailCategoryMappings.size()).isEqualTo(mockProductDetailCategories.size());
//
//        assertThat(response).isEqualTo(tempProductId);
    }

    @Test
    void 할인__판매_예정_상품_등록() {
//        var tempProductId = 1L;
//        var name = "test product";
//        var description = "test description";
//        var mainCategory = ProductMainCategory.GAME;
//        var price = 10000;
//        var quantity = 100;
//        var stockStatus = ProductStockStatus.TO_BE_SOLD;
//        var type = ProductType.ANALOGUE;
//        var saleStartDate = LocalDateTime.now().plusDays(7);
//        var discountRate = 20;
//        var discountStartDate = LocalDateTime.now().plusDays(7);
//        var discountEndDate = LocalDateTime.now().plusDays(14);
//
//        var productRegisterRequest = RegisterProductRequest.builder()
//                .shopId(SHOP_ID)
//                .email(ACCOUNT_EMAIL)
//                .name(name)
//                .mainCategory(mainCategory)
//                .description(description)
//                .price(price)
//                .quantity(quantity)
//                .status(stockStatus)
//                .type(type)
//                .expectedSaleDate(saleStartDate)
//                .discountRate(discountRate)
//                .discountStartDate(discountStartDate)
//                .discountEndDate(discountEndDate)
//                .build();
//
//        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
//        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
//        given(mockProductRepository.save(productArgumentCaptor.capture())).willReturn(mockProduct);
//        given(mockProduct.getId()).willReturn(tempProductId);
//
//        var response = sellerProductRegistrationService.registerProduct(productRegisterRequest);
//
//        then(mockShopRepository).should(times(1)).findById(anyLong());
//        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
//        then(mockProductRepository).should(times(1)).save(any(Product.class));
//        then(mockProductCustomCategoryRepository).shouldHaveNoInteractions();
//
//        var product = productArgumentCaptor.getValue();
//
//        assertThat(product.getName()).isEqualTo(name);
//        assertThat(product.getStockStatus()).isEqualTo(stockStatus);
//        assertThat(product.getType()).isEqualTo(type);
//        assertThat(product.getRemainingQuantity()).isEqualTo(quantity);
//        assertThat(product.getOriginalPrice()).isEqualTo(price);
//        assertThat(product.getShop()).isEqualTo(mockShop);
//        assertThat(product.getCustomCategories()).isNull();
//        assertThat(product.getSaleStartDate()).isNotNull();
//        assertThat(product.getSaleEndDate()).isNull();
//        assertThat(product.getDiscountRate()).isEqualTo(discountRate);
//        assertThat(product.getDiscountStartDate()).isEqualTo(discountStartDate);
//        assertThat(product.getDiscountEndDate()).isEqualTo(discountEndDate);
//
//        assertThat(response).isEqualTo(tempProductId);
    }

    @Test
    void 상점_주인_계정이_아니면_상품을_등록_할수없음() {
//        var mockProductRegisterRequest = mock(RegisterProductRequest.class);
//
//        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
//        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
//        given(mockProductRegisterRequest.shopId()).willReturn(SHOP_ID);
//        given(mockProductRegisterRequest.adminMemberId()).willReturn(ACCOUNT_EMAIL);
//        doThrow(InvalidShopOwnerException.class).when(mockShop).ownerCheck(mockAccount);
//
//        assertThatThrownBy(() -> sellerProductRegistrationService.registerProduct(mockProductRegisterRequest))
//                .isExactlyInstanceOf(InvalidShopOwnerException.class);
//
//        then(mockShopRepository).should(times(1)).findById(anyLong());
//        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
//        then(mockProductRepository).shouldHaveNoInteractions();
//        then(mockProductCustomCategoryRepository).shouldHaveNoInteractions();
    }

    @Test
    void 등록된_상품_리스트_페이징_조회() {

    }

    @Test
    void 상품_정보_수정() {
//        var modifyProductId = 1L;
//        var modifyName = "test modify product";
//        var modifyDescription = "test modify description";
//        var modifyMainCategory = ProductMainCategory.IDOL;
//        var modifyType = ProductType.ANALOGUE_EXCLUSIVE;
//
//        var productMetadataModifyInfo = UpdateProductMetadataRequest.builder()
//                .shopId(SHOP_ID)
//                .modifyProductId(modifyProductId)
//                .email(ACCOUNT_EMAIL)
//                .modifyName(modifyName)
//                .modifyDescription(modifyDescription)
//                .modifyTopCategory(modifyMainCategory)
//                .modifyType(modifyType)
//                .build();
//
//        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
//        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
//        given(mockProductRepository.findById(anyLong())).willReturn(Optional.of(mockProduct));
//
//        sellerProductRegistrationService.updateProductMetadata(productMetadataModifyInfo);
//
//        then(mockShopRepository).should(times(1)).findById(anyLong());
//        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
//        then(mockProductRepository).should(times(1)).findById(anyLong());
//        then(mockProductCustomCategoryRepository).shouldHaveNoInteractions();
//        then(mockProduct).should(times(1)).updateMetadata(modifyName, modifyDescription, modifyType, modifyMainCategory, null);
    }

    @Test
    void 상품_삭제() {
//        var deleteProductId = 1L;
//
//        given(mockShopRepository.findById(anyLong())).willReturn(Optional.of(mockShop));
//        given(mockAccountRepository.findByEmail(anyString())).willReturn(Optional.of(mockAccount));
//        given(mockProductRepository.findById(anyLong())).willReturn(Optional.of(mockProduct));
//
//        sellerProductRegistrationService.delete(SHOP_ID, ACCOUNT_EMAIL, deleteProductId);
//
//        then(mockShopRepository).should(times(1)).findById(anyLong());
//        then(mockAccountRepository).should(times(1)).findByEmail(anyString());
//        then(mockProductRepository).should(times(1)).findById(anyLong());
//        then(mockProductRepository).should(times(1)).delete(any(Product.class));
    }


}