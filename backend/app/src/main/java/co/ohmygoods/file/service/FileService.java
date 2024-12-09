package co.ohmygoods.file.service;

import co.ohmygoods.file.model.entity.File;
import co.ohmygoods.file.repository.FileRepository;
import co.ohmygoods.file.service.dto.UploadFileRequest;
import co.ohmygoods.file.service.dto.UploadFileResponse;
import co.ohmygoods.file.model.vo.CloudStorageProvider;
import co.ohmygoods.file.model.vo.DomainType;
import co.ohmygoods.file.model.vo.StorageStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final List<FileStorageService> fileStorageServices;

    public Optional<URI> getCloudStorageAccessURL(UploadFileRequest request) {
        return findSupportFileStorageService(request.storageStrategy(), request.cloudStorageProvider())
                .map(FileStorageService::getCloudStorageAccessURL)
                .map(cloudStorageAccessURL -> {

                    if (request.domainIds() != null && !request.domainIds().isEmpty()) {
                        List<File> files = request.domainIds()
                                .stream()
                                .map(domainId -> {
                                    return File.builder()
                                            .uploaderEmail(request.uploaderEmail())
                                            .domainType(request.targetDomain())
                                            .domainId(domainId)
                                            .storageStrategy(request.storageStrategy())
                                            .cloudStorageProvider(request.cloudStorageProvider())
                                            .storagePath(cloudStorageAccessURL.toString())
                                            .build();
                                })
                                .toList();

                        fileRepository.saveAll(files);
                    }
                    return cloudStorageAccessURL;
                })
                .orElseGet(Optional::empty);
    }

    public void upload(UploadFileRequest request) {
        findSupportFileStorageService(request.storageStrategy(), request.cloudStorageProvider())
                .ifPresent(service -> {
                    Collection<UploadFileResponse> uploadResponse = service.upload(request);

                    List<File> files = uploadResponse.stream()
                            .map(response -> {
                                return File.builder()
                                        .uploaderEmail(request.uploaderEmail())
                                        .domainType(request.targetDomain())
                                        .domainId(response.uploadedDomainId())
                                        .storageStrategy(request.storageStrategy())
                                        .cloudStorageProvider(request.cloudStorageProvider())
                                        .fileName(response.uploadedFileName())
                                        .contentType(response.uploadedFileContentType())
                                        .storagePath(response.uploadedFilePath())
                                        .build();
                            }).toList();

                    fileRepository.saveAll(files);
                });
    }

    public Collection<Optional<InputStream>> download(DomainType domainType, Collection<String> domainIds) {
        List<File> files = fileRepository.findAllByDomainTypeAndDomainIds(domainType, domainIds);

        return files.isEmpty()
                ? Collections.emptyList()
                : findSupportFileStorageService(files.getFirst().getStorageStrategy(), files.getFirst().getCloudStorageProvider())
                        .map(service -> service.downloadAll(getStoragePath(files)))
                        .orElseGet(Collections::emptyList);
    }

    public void delete(DomainType domainType, Collection<String> domainIds) {
        List<File> files = fileRepository.findAllByDomainTypeAndDomainIds(domainType, domainIds);

        if (!files.isEmpty()) {
            File first = files.getFirst();

            findSupportFileStorageService(first.getStorageStrategy(), first.getCloudStorageProvider())
                    .ifPresent(service -> service.deleteAll(getStoragePath(files)));
        }
    }

    private static List<String> getStoragePath(List<File> files) {
        return files.stream().map(File::getStoragePath).toList();
    }

    private Optional<FileStorageService> findSupportFileStorageService(StorageStrategy storageStrategy, CloudStorageProvider cloudStorageProvider) {
        return fileStorageServices.stream()
                .filter(service -> service.canSupport(storageStrategy, cloudStorageProvider))
                .findFirst();
    }

}
