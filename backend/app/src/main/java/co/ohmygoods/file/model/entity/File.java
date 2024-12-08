package co.ohmygoods.file.model.entity;

import co.ohmygoods.file.model.vo.CloudStorageProvider;
import co.ohmygoods.file.model.vo.DomainType;
import co.ohmygoods.file.model.vo.FileType;
import co.ohmygoods.file.model.vo.StorageStrategy;
import co.ohmygoods.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uploaderEmail;

    @Column(nullable = false, updatable = false)
    private String domainId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DomainType domainType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StorageStrategy storageStrategy;

    @Enumerated(EnumType.STRING)
    private CloudStorageProvider cloudStorageProvider;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private String storagePath;
}
