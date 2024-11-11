package co.ohmygoods.shop.seller;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.persistence.AccountRepository;
import co.ohmygoods.shop.entity.Shop;
import co.ohmygoods.shop.exception.InvalidShopOwnerException;
import co.ohmygoods.shop.exception.UnchangeableShopOwnerException;
import co.ohmygoods.shop.repository.ShopRepository;
import co.ohmygoods.shop.seller.dto.ShopOwnerChangeHistoryDto;
import co.ohmygoods.shop.seller.entity.ShopOwnerChangeHistory;
import co.ohmygoods.shop.seller.vo.ShopOwnerStatus;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ShopOwnerChangeServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ShopOwnerChangeHistoryRepository shopOwnerChangeHistoryRepository;

    @InjectMocks
    private ShopOwnerChangeService shopOwnerChangeService;

    @Mock
    private OAuth2Account fooMockAccount;

    @Mock
    private OAuth2Account barMockAccount;

    @Mock
    private Shop mockShop;

    @Mock
    private ShopOwnerChangeHistory mockShopOwnerChangeHistory;

    @Test
    void 상점_주인_변경_신청() {
        var fooEmail = "fooEmail@email.com";
        var barEmail = "barEmail@email.com";

        when(accountRepository.findByEmail(fooEmail)).thenReturn(Optional.of(fooMockAccount));
        when(accountRepository.findByEmail(barEmail)).thenReturn(Optional.of(barMockAccount));
        when(shopRepository.findById(anyLong())).thenReturn(Optional.of(mockShop));

        shopOwnerChangeService.request(fooEmail, barEmail, anyLong());

        then(accountRepository).should(times(2)).findByEmail(anyString());
        then(shopRepository).should(times(1)).findById(anyLong());
        then(mockShop).should(times(1)).ownerCheck(fooMockAccount);
        then(shopOwnerChangeHistoryRepository).should(times(1)).save(any(ShopOwnerChangeHistory.class));
    }

    @Test
    void 상점_주인이_아닌_경우_변경_신청_불가() {
        var fooEmail = "fooEmail@email.com";
        var barEmail = "barEmail@email.com";
        var expectedException = InvalidShopOwnerException.class;

        when(accountRepository.findByEmail(fooEmail)).thenReturn(Optional.of(fooMockAccount));
        when(accountRepository.findByEmail(barEmail)).thenReturn(Optional.of(barMockAccount));
        when(shopRepository.findById(anyLong())).thenReturn(Optional.of(mockShop));
        doThrow(expectedException).when(mockShop).ownerCheck(fooMockAccount);

        assertThatThrownBy(() -> shopOwnerChangeService.request(fooEmail, barEmail, anyLong()))
                .isExactlyInstanceOf(expectedException);

        then(accountRepository).should(times(2)).findByEmail(anyString());
        then(shopRepository).should(times(1)).findById(anyLong());
        then(mockShop).should(times(1)).ownerCheck(fooMockAccount);
        then(shopOwnerChangeHistoryRepository).shouldHaveNoInteractions();
    }

    @Test
    void 상점_주인_변경_신청_취소() {
        var expectedShopOwnerStatus = ShopOwnerStatus.OWNER_CHANGE_CANCELED;

        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.of(fooMockAccount));
        when(shopOwnerChangeHistoryRepository.findById(anyLong())).thenReturn(Optional.of(mockShopOwnerChangeHistory));
        when(mockShopOwnerChangeHistory.getShop()).thenReturn(mockShop);

        shopOwnerChangeService.cancel("fooMockAccountEmail", anyLong());

        then(accountRepository).should(times(1)).findByEmail(anyString());
        then(shopOwnerChangeHistoryRepository).should(times(1)).findById(anyLong());
        then(mockShop).should(times(1)).ownerCheck(fooMockAccount);
        then(mockShopOwnerChangeHistory).should(times(1)).changeShopOwnerStatus(expectedShopOwnerStatus);
    }

    @Test
    void 이미_수락한_경우_취소_불가() {
        var spyShopOwnerChangeHistory = spy(ShopOwnerChangeHistory.class);

        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.of(fooMockAccount));
        when(shopOwnerChangeHistoryRepository.findById(anyLong())).thenReturn(Optional.of(spyShopOwnerChangeHistory));
        when(spyShopOwnerChangeHistory.getShop()).thenReturn(mockShop);
        when(spyShopOwnerChangeHistory.getStatus()).thenReturn(ShopOwnerStatus.OWNER_CHANGE_APPROVED);

        assertThatThrownBy(() -> shopOwnerChangeService.cancel("fooMockAccountEmail", anyLong()))
                .isExactlyInstanceOf(UnchangeableShopOwnerException.class);

        then(accountRepository).should(times(1)).findByEmail(anyString());
        then(shopOwnerChangeHistoryRepository).should(times(1)).findById(anyLong());
    }

    @Test
    void 상점_주인_변경_요청_수락() {
        var expectedShopOwnerStatus = ShopOwnerStatus.OWNER_CHANGE_APPROVED;

        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.of(barMockAccount));
        when(shopOwnerChangeHistoryRepository.findById(anyLong())).thenReturn(Optional.of(mockShopOwnerChangeHistory));
        when(mockShopOwnerChangeHistory.getShop()).thenReturn(mockShop);

        shopOwnerChangeService.approve("barAccountEmail", anyLong());

        then(accountRepository).should(times(1)).findByEmail(anyString());
        then(shopOwnerChangeHistoryRepository).should(times(1)).findById(anyLong());
        then(mockShopOwnerChangeHistory).should(times(1)).targetAccountCheck(barMockAccount);
        then(mockShopOwnerChangeHistory).should(times(1)).changeShopOwnerStatus(expectedShopOwnerStatus);
        then(mockShop).should(times(1)).changeOwner(barMockAccount);
    }

    @Test
    void 상점_주인_변경_요청_거절() {
        var expectedShopOwnerStatus = ShopOwnerStatus.OWNER_CHANGE_REJECTED;

        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.of(barMockAccount));
        when(shopOwnerChangeHistoryRepository.findById(anyLong())).thenReturn(Optional.of(mockShopOwnerChangeHistory));

        shopOwnerChangeService.reject("barAccountEmail", anyLong());

        then(mockShopOwnerChangeHistory).should(times(1)).targetAccountCheck(barMockAccount);
        then(mockShopOwnerChangeHistory).should(times(1)).changeShopOwnerStatus(expectedShopOwnerStatus);
    }

    @Test
    void 이미_취소한_경우_수락_불가() {
        var spyShopOwnerChangeHistory = spy(ShopOwnerChangeHistory.class);

        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.of(fooMockAccount));
        when(shopOwnerChangeHistoryRepository.findById(anyLong())).thenReturn(Optional.of(spyShopOwnerChangeHistory));
        when(spyShopOwnerChangeHistory.getShop()).thenReturn(mockShop);
        doNothing().when(spyShopOwnerChangeHistory).targetAccountCheck(fooMockAccount);
        when(spyShopOwnerChangeHistory.getStatus()).thenReturn(ShopOwnerStatus.OWNER_CHANGE_CANCELED);

        assertThatThrownBy(() -> shopOwnerChangeService.approve("fooMockAccountEmail", anyLong()))
                .isExactlyInstanceOf(UnchangeableShopOwnerException.class);

        then(accountRepository).should(times(1)).findByEmail(anyString());
        then(shopOwnerChangeHistoryRepository).should(times(1)).findById(anyLong());
    }

    @Test
    void 상점_주인_변경_내역_조회() {
        var requestAccountEmail = "fooAccountEmail@email.com";
        var targetAccountEmail = "barAccountEmail@email.com";
        var shopOwnerStatus = ShopOwnerStatus.OWNER_CHANGE_REQUESTED;
        var createdAt = LocalDateTime.now();

        when(mockShopOwnerChangeHistory.getOriginalOwner()).thenReturn(spy(OAuth2Account.class));
        when(mockShopOwnerChangeHistory.getTargetAccount()).thenReturn(spy(OAuth2Account.class));
        when(mockShopOwnerChangeHistory.getOriginalOwner().getEmail()).thenReturn(requestAccountEmail);
        when(mockShopOwnerChangeHistory.getTargetAccount().getEmail()).thenReturn(targetAccountEmail);
        when(mockShopOwnerChangeHistory.getStatus()).thenReturn(shopOwnerStatus);
        when(mockShopOwnerChangeHistory.getCreatedAt()).thenReturn(createdAt);
        when(shopOwnerChangeHistoryRepository.findById(anyLong())).thenReturn(Optional.of(mockShopOwnerChangeHistory));

        var history = shopOwnerChangeService.getHistory(anyLong());

        then(shopOwnerChangeHistoryRepository).should(times(1)).findById(anyLong());
        assertThat(history.requestAccountEmail()).isEqualTo(requestAccountEmail);
        assertThat(history.targetAccountEmail()).isEqualTo(targetAccountEmail);
        assertThat(history.status()).isEqualTo(shopOwnerStatus);
        assertThat(history.applicationDate()).isEqualTo(createdAt);

    }
}