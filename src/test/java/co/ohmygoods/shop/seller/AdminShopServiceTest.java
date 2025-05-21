package co.ohmygoods.shop.seller;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.shop.service.admin.AdminShopService;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.repository.ShopRepository;
import co.ohmygoods.shop.service.admin.dto.ShopCreateRequest;
import co.ohmygoods.shop.model.vo.ShopStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@Disabled("테스트 코드 리팩토링 필요")
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminShopServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private AdminShopService adminShopService;

    @Mock
    private Shop mockShop;

    @Mock
    private Account mockAccount;

    private ShopCreateRequest shopCreateRequest;
    private static final String MOCK_ACCOUNT_EMAIL = "mockAccount@test.com";

    @BeforeEach
    void init() {
        shopCreateRequest = new ShopCreateRequest("testEmail", "testShop", "testShopIntroduction");
    }

    @Test
    void 상점_생성() {
        var shopId = 1L;

        when(shopRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        when(accountRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(mockAccount));

        when(shopRepository.save(any(Shop.class)))
                .thenReturn(mockShop);

        when(mockShop.getId())
                .thenReturn(shopId);

        var createdShopId = adminShopService.createShop(shopCreateRequest);

        then(shopRepository).should(times(1)).findByName(anyString());
        then(accountRepository).should(times(1)).findByEmail(anyString());
        then(shopRepository).should(times(1)).save(any(Shop.class));

        assertThat(createdShopId).isEqualTo(shopId);
    }

    @Test
    void 중복된_이름의_상점_생성_불가능() {
        when(shopRepository.findByName(anyString()))
                .thenReturn(Optional.of(mockShop));

//        assertThatThrownBy(() -> sellerShopService.createShop(createShopRequest))
//                .isExactlyInstanceOf(InvalidShopNameException.class);

//        then(shopRepository).should(times(1)).findByName(anyString());
//        then(accountRepository).shouldHaveNoInteractions();
//        then(shopRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void 상점_비활성화() {
        var expectedShopStatus = ShopStatus.INACTIVE;

        when(shopRepository.findById(anyLong()))
                .thenReturn(Optional.of(mockShop));

        adminShopService.inactiveShop(anyString());

        then(shopRepository).should(times(1)).findById(anyLong());
        then(mockShop).should(times(1)).changeShopStatus(expectedShopStatus);
    }

    @Test
    void 삭제된_상태의_상점은_비활성화_할수없음() {
//        when(shopRepository.findById(anyLong()))
//                .thenReturn(Optional.of(mockShop));
//
//        doThrow(new UnchangeableShopStatusException("Shop status cannot be changed"))
//                .when(mockShop).changeShopStatus(ShopStatus.INACTIVE);
//
//        assertThatThrownBy(() -> sellerShopService.inactiveShop(anyString()))
//                .isExactlyInstanceOf(UnchangeableShopStatusException.class);
//
//        then(shopRepository).should(times(1)).findById(anyLong());
//        then(mockShop).should(times(1)).changeShopStatus(ShopStatus.INACTIVE);
//        assertThat(mockShop.getStatus()).isNotEqualTo(ShopStatus.INACTIVE);
    }

}