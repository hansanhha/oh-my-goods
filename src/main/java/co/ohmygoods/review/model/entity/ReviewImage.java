package co.ohmygoods.review.model.entity;

import co.ohmygoods.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID imageId;

    private int imageOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalFileName;

    @Setter
    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String contentType;

    public static ReviewImage create(int order, Review review, MultipartFile file) {
        return new ReviewImage(0L, UUID.randomUUID(), order, review, file.getName(), file.getOriginalFilename(),
                null, file.getSize(), file.getContentType());
    }
}
