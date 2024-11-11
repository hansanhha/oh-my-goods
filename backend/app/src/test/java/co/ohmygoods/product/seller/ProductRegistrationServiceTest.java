package co.ohmygoods.product.seller;

import co.ohmygoods.auth.account.persistence.AccountRepository;
import co.ohmygoods.product.repository.ProductDetailCategoryRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.product.repository.ProductSeriesRepository;
import co.ohmygoods.shop.repository.ShopRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRegistrationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDetailCategoryRepository productDetailCategoryRepository;

    @Mock
    private ProductSeriesRepository productSeriesRepository;

    @InjectMocks
    private ProductRegistrationService productRegistrationService;

    @Test
    void 상품_등록() {

    }

    @Test
    void 상점_주인_계정이_아니면_상품을_등록_할수없음() {

    }

    @Test
    void 등록된_상품_리스트_페이징_조회() {

    }

    @Test
    void 상품_정보_수정() {

    }

    @Test
    void 상품_삭제() {

    }


}