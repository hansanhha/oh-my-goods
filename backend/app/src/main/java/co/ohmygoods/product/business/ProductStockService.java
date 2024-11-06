package co.ohmygoods.product.business;

import co.ohmygoods.product.ProductRepository;
import co.ohmygoods.product.exception.ProductNotFoundException;
import co.ohmygoods.product.vo.ProductStockStatus;
import co.ohmygoods.shop.ShopRepository;
import co.ohmygoods.shop.exception.ShopNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductStockService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    public void updateStock(Long shopId, Long productId, int quantity) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        product.shopCheck(shop);
        product.updateRemainingQuantity(quantity);
    }

    public void increaseStock(Long shopId, Long productId, int quantity) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        product.shopCheck(shop);
        product.updateRemainingQuantity(Math.min(product.getRemainingQuantity() + quantity, Integer.MAX_VALUE));
    }

    public void decreaseStock(Long shopId, Long productId, int quantity) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        product.shopCheck(shop);
        product.updateRemainingQuantity(Math.max(product.getRemainingQuantity() - quantity, 0));
    }

    public void updatePurchaseMaximumQuantity(Long shopId, Long productId, int maximumQuantity) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        product.shopCheck(shop);
        product.updatePurchaseMaximumQuantity(Math.max(maximumQuantity, 1));
    }

    public void updateOnSalesStatus(Long shopId, Long productId) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        product.shopCheck(shop);
        product.updateStockStatus(ProductStockStatus.ON_SALES);
    }

    public void updateToBeSoldStatus(Long shopId, Long productId) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        product.shopCheck(shop);
        product.updateStockStatus(ProductStockStatus.TO_BE_SOLD);
    }

    public void updateSoldOutStatus(Long shopId, Long productId) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        product.shopCheck(shop);
        product.updateStockStatus(ProductStockStatus.SOLDOUT);
    }

    public void updateToBeRestockedStatus(Long shopId, Long productId) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        product.shopCheck(shop);
        product.updateStockStatus(ProductStockStatus.TO_BE_RESTOCKED);
    }

    public void updateRestockedStatus(Long shopId, Long productId) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        product.shopCheck(shop);
        product.updateStockStatus(ProductStockStatus.RESTOCKED);
    }
}
