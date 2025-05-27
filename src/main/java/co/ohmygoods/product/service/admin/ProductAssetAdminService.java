package co.ohmygoods.product.service.admin;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.global.file.model.vo.CloudStorageProvider;
import co.ohmygoods.global.file.model.vo.DomainType;
import co.ohmygoods.global.file.service.FileService;
import co.ohmygoods.global.file.service.dto.UploadFileRequest;
import co.ohmygoods.global.file.service.dto.UploadFileResponse;
import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductAssetInfo;
import co.ohmygoods.product.repository.ProductAssetInfoRepository;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.shop.exception.ShopException;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductAssetAdminService {

    private final FileService fileService;
    private final ShopRepository shopRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final ProductAssetInfoRepository productAssetInfoRepository;


    public void upload(Long productId, String sellerMemberId, MultipartFile[] files) {
        Shop shop = shopRepository.findByAdminMemberId(sellerMemberId).orElseThrow(ShopException::notFoundShop);
        Account account = accountRepository.findByMemberId(sellerMemberId).orElseThrow(AuthException::notFoundAccount);
        Product product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        product.shopCheck(shop);

        Map<String, ProductAssetInfo> productAssetInfoMap = new HashMap<>(files.length);
        Map<String, MultipartFile> productAssetFileMap = new HashMap<>(files.length);

        for (int i = 0; i < files.length; i++) {
            ProductAssetInfo productAssetInfo = ProductAssetInfo.create(UUID.randomUUID(), i, product, files[i]);
            productAssetInfoMap.put(productAssetInfo.getAssetId().toString(), productAssetInfo);
            productAssetFileMap.put(productAssetInfo.getAssetId().toString(), files[i]);
        }

        List<UploadFileResponse> uploadResponses = fileService.upload(UploadFileRequest.useCloudStorage(
                CloudStorageProvider.DEFAULT, account.getEmail(), DomainType.SELLER, productAssetFileMap));

        uploadResponses.forEach(response ->
            productAssetInfoMap.get(response.uploadedDomainId()).setPath(response.uploadedFilePath()));

        productAssetInfoRepository.saveAll(productAssetInfoMap.values());
    }

    public void replace(Long productId, String sellerMemberId, MultipartFile[] replaceFiles) {
        Shop shop = shopRepository.findByAdminMemberId(sellerMemberId).orElseThrow(ShopException::notFoundShop);
        Account account = accountRepository.findByMemberId(sellerMemberId).orElseThrow(AuthException::notFoundAccount);
        Product product = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);
        List<ProductAssetInfo> deleteProductAssetInfos = productAssetInfoRepository.findByProduct(product);

        product.shopCheck(shop);

        fileService.delete(DomainType.SELLER, deleteProductAssetInfos.stream()
                .map(ProductAssetInfo::getAssetId).map(String::valueOf).toList());

        Map<String, ProductAssetInfo> replaceProductAssetInfoMap = new HashMap<>(replaceFiles.length);
        Map<String, MultipartFile> replaceProductAssetFileMap = new HashMap<>(replaceFiles.length);

        for (int i = 0; i < replaceFiles.length; i++) {
            ProductAssetInfo productAssetInfo = ProductAssetInfo.create(UUID.randomUUID(), i, product, replaceFiles[i]);
            replaceProductAssetInfoMap.put(productAssetInfo.getAssetId().toString(), productAssetInfo);
            replaceProductAssetFileMap.put(productAssetInfo.getAssetId().toString(), replaceFiles[i]);
        }

        List<UploadFileResponse> uploadResponses = fileService.upload(UploadFileRequest.useCloudStorage(
                CloudStorageProvider.DEFAULT, account.getEmail(), DomainType.SELLER, replaceProductAssetFileMap));

        uploadResponses.forEach(response ->
                replaceProductAssetInfoMap.get(response.uploadedDomainId()).setPath(response.uploadedFilePath()));

        productAssetInfoRepository.saveAll(replaceProductAssetInfoMap.values());
    }
}
