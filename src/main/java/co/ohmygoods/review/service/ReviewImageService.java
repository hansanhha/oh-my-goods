package co.ohmygoods.review.service;


import co.ohmygoods.global.file.model.vo.CloudStorageProvider;
import co.ohmygoods.global.file.model.vo.DomainType;
import co.ohmygoods.global.file.model.vo.StorageStrategy;
import co.ohmygoods.global.file.service.FileService;
import co.ohmygoods.global.file.service.dto.UploadFileRequest;
import co.ohmygoods.global.file.service.dto.UploadFileResponse;
import co.ohmygoods.review.model.entity.Review;
import co.ohmygoods.review.model.entity.ReviewImage;
import co.ohmygoods.review.repository.ReviewImageRepository;
import co.ohmygoods.review.repository.ReviewRepository;

import java.util.HashMap;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@Transactional
@RequiredArgsConstructor
public class ReviewImageService {

    private final FileService fileService;
    private final ReviewImageRepository reviewImageRepository;

    public void upload(Review review, String memberId, StorageStrategy storageStrategy, List<MultipartFile> files) {
        HashMap<String, ReviewImage> reviewImages = new HashMap<>();
        HashMap<String, MultipartFile> uploadImages = new HashMap<>(files.size());

        for (int i = 0; i < files.size(); i++) {
            ReviewImage reviewImage = ReviewImage.create(i, review, files.get(i));
            reviewImages.put(reviewImage.getImageId().toString(), reviewImage);
            uploadImages.put(reviewImage.getImageId().toString(), files.get(i));
        }

        List<UploadFileResponse> imageUploadResponses = fileService.upload(UploadFileRequest.from(storageStrategy, CloudStorageProvider.DEFAULT,
                memberId, DomainType.REVIEW, null, uploadImages));

        imageUploadResponses.forEach(r -> reviewImages.get(r.uploadedDomainId()).setPath(r.uploadedFilePath()));

        reviewImageRepository.saveAll(reviewImages.values());
    }

    public void delete(Review review) {
        List<ReviewImage> reviewImages = reviewImageRepository.findAllByReview(review);

        if (!reviewImages.isEmpty()) {
            List<String> imageIds = reviewImages.stream().map(info -> info.getImageId().toString()).toList();
            fileService.delete(DomainType.REVIEW, imageIds);
            reviewImageRepository.deleteAll(reviewImages);
        }
    }
}
