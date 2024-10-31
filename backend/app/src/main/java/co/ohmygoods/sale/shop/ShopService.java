package co.ohmygoods.sale.shop;

import co.ohmygoods.sale.shop.dto.ShopCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopService {

    public Long create(ShopCreationRequest request) {
        return 0L;
    }

    public void inactive() {

    }

    public void delete() {

    }

    public void requestTransfer() {

    }

    public void cancelTransferRequest() {

    }

    public void approveTransfer() {

    }

    public void rejectTransfer() {

    }

}
