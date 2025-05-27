package co.ohmygoods.review.service;

import co.ohmygoods.global.file.model.vo.CloudStorageProvider;
import co.ohmygoods.global.file.model.vo.DomainType;
import co.ohmygoods.global.file.model.vo.StorageStrategy;
import co.ohmygoods.global.file.service.FileService;
import co.ohmygoods.global.file.service.dto.UploadFileRequest;
import co.ohmygoods.global.file.service.dto.UploadFileResponse;
import co.ohmygoods.review.exception.ReviewException;
import co.ohmygoods.review.model.entity.Review;
import co.ohmygoods.review.model.entity.ReviewImageInfo;
import co.ohmygoods.review.repository.ReviewImageInfoRepository;
import co.ohmygoods.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewImageService {

    private final FileService fileService;
    private final ReviewRepository reviewRepository;
    private final ReviewImageInfoRepository reviewImageInfoRepository;

    public void upload(Long reviewId, String accountEmail, StorageStrategy storageStrategy, List<MultipartFile> imageFiles) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewException::notFoundReview);

        HashMap<String, MultipartFile> imageInfoIdFileMap = new HashMap<>(imageFiles.size());

        // ReviewImageInfo 엔티티 생성 및 파일 업로드 정보 추가
        List<ReviewImageInfo> savedReviewImageInfos = IntStream.range(0, imageFiles.size())
                .mapToObj(i -> {
                    UUID uuid = UUID.randomUUID();
                    MultipartFile imageFile = imageFiles.get(i);

                    ReviewImageInfo reviewImageInfo = ReviewImageInfo.create(uuid, i, review, imageFile);
                    imageInfoIdFileMap.put(uuid.toString(), imageFile);

                    return reviewImageInfo;
                }).toList();

        // 실제 파일 업로드
        List<UploadFileResponse> imageUploadResponse = fileService.upload(UploadFileRequest.from(storageStrategy, CloudStorageProvider.DEFAULT,
                accountEmail, DomainType.REVIEW, null, imageInfoIdFileMap));

        // ReviewImageInfo 엔티티에 업로드된 파일 경로 추가
        imageUploadResponse.forEach(response -> {
            Optional<ReviewImageInfo> matched = savedReviewImageInfos.stream()
                    .filter(imageInfo -> imageInfo.getImageId().toString()
                            .equals(response.uploadedDomainId()))
                    .findFirst();

            if (matched.isPresent()) {
                ReviewImageInfo reviewImageInfo = matched.get();
                reviewImageInfo.setPath(response.uploadedFilePath());
            }
        });
    }

    public void delete(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewException::notFoundReview);
        List<ReviewImageInfo> reviewImageInfos = reviewImageInfoRepository.findAllByReview(review);

        if (!reviewImageInfos.isEmpty()) {
            List<String> imageIds = reviewImageInfos.stream()
                    .map(info -> info.getImageId().toString()).toList();
            fileService.delete(DomainType.REVIEW, imageIds);
            reviewImageInfoRepository.deleteAll(reviewImageInfos);
        }
    }
}
