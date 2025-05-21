package co.ohmygoods.product.service.admin;


import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.shop.exception.ShopException;
import co.ohmygoods.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductStockAdminService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    public void updateStock(String ownerMemberId, Long productId, int quantity) {
        var shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);
        product.updateRemainingQuantity(quantity);
    }

    public void increaseStock(String ownerMemberId, Long productId, int quantity) {
        var shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);
        product.updateRemainingQuantity(Math.min(product.getRemainingQuantity() + quantity, Integer.MAX_VALUE));
    }

    public void decreaseStock(String ownerMemberId, Long productId, int quantity) {
        var shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);
        product.updateRemainingQuantity(Math.max(product.getRemainingQuantity() - quantity, 0));
    }

    public void updatePurchaseMaximumQuantity(String ownerMemberId, Long productId, int maximumQuantity) {
        var shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);
        product.updatePurchaseMaximumQuantity(Math.max(maximumQuantity, 1));
    }

    public void updateOnSalesStatus(String ownerMemberId, Long productId) {
        var shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);
        product.updateStockStatus(ProductStockStatus.ON_SALES);
    }

    public void updateToBeSoldStatus(String ownerMemberId, Long productId) {
        var shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);
        product.updateStockStatus(ProductStockStatus.TO_BE_SOLD);
    }

    public void updateSoldOutStatus(String ownerMemberId, Long productId) {
        var shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);
        product.updateStockStatus(ProductStockStatus.SOLDOUT);
    }

    public void updateToBeRestockedStatus(String ownerMemberId, Long productId) {
        var shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);
        product.updateStockStatus(ProductStockStatus.TO_BE_RESTOCKED);
    }

    public void updateRestockedStatus(String ownerMemberId, Long productId) {
        var shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);
        var product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);
        product.updateStockStatus(ProductStockStatus.RESTOCKED);
    }
}
