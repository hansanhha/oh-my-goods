package co.ohmygoods.product.service.admin;


import co.ohmygoods.global.file.model.vo.CloudStorageProvider;
import co.ohmygoods.global.file.model.vo.DomainType;
import co.ohmygoods.global.file.service.FileService;
import co.ohmygoods.global.file.service.dto.UploadFileRequest;
import co.ohmygoods.global.file.service.dto.UploadFileResponse;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductAsset;
import co.ohmygoods.product.repository.ProductAssetInfoRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductAssetAdminService {

    private final FileService fileService;
    private final ProductAssetInfoRepository productAssetInfoRepository;


    public void upload(Product product, String adminMemberId, List<MultipartFile> files) {
        Map<String, ProductAsset> productAssets = new HashMap<>(files.size());
        Map<String, MultipartFile> uploadFiles = new HashMap<>(files.size());

        for (int i = 0; i < files.size(); i++) {
            ProductAsset productAsset = ProductAsset.create(i, product, files.get(i));
            productAssets.put(productAsset.getAssetId().toString(), productAsset);
            uploadFiles.put(productAsset.getAssetId().toString(), files.get(i));
        }

        List<UploadFileResponse> uploadResponses = fileService.upload(UploadFileRequest.useCloudStorage(
                CloudStorageProvider.DEFAULT, adminMemberId, DomainType.SELLER, uploadFiles));

        uploadResponses.forEach(response ->
            productAssets.get(response.uploadedDomainId()).setPath(response.uploadedFilePath()));

        productAssetInfoRepository.saveAll(productAssets.values());
    }

    public void replace(Product product, String adminMemberId, List<MultipartFile> replaceFiles) {
        List<ProductAsset> deleteProductAssets = productAssetInfoRepository.findByProduct(product);

        fileService.delete(DomainType.SELLER, deleteProductAssets.stream()
                .map(ProductAsset::getAssetId).map(String::valueOf).toList());

        Map<String, ProductAsset> productAssets = new HashMap<>(replaceFiles.size());
        Map<String, MultipartFile> reuploadFiles = new HashMap<>(replaceFiles.size());

        for (int i = 0; i < replaceFiles.size(); i++) {
            ProductAsset productAsset = ProductAsset.create(i, product, replaceFiles.get(i));
            productAssets.put(productAsset.getAssetId().toString(), productAsset);
            reuploadFiles.put(productAsset.getAssetId().toString(), replaceFiles.get(i));
        }

        List<UploadFileResponse> uploadResponses = fileService.upload(UploadFileRequest.useCloudStorage(
                CloudStorageProvider.DEFAULT, adminMemberId, DomainType.SELLER, reuploadFiles));

        uploadResponses.forEach(response ->
                productAssets.get(response.uploadedDomainId()).setPath(response.uploadedFilePath()));

        productAssetInfoRepository.saveAll(productAssets.values());
    }

    public void delete(Product product) {
        List<ProductAsset> deleteProductAssets = productAssetInfoRepository.findByProduct(product);

        fileService.delete(DomainType.SELLER, deleteProductAssets.stream()
                .map(ProductAsset::getAssetId).map(String::valueOf).toList());
    }

}
