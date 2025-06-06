package co.ohmygoods.global.file.service;

import co.ohmygoods.global.file.exception.FileException;
import co.ohmygoods.global.file.model.entity.File;
import co.ohmygoods.global.file.model.vo.CloudStorageProvider;
import co.ohmygoods.global.file.model.vo.DomainType;
import co.ohmygoods.global.file.model.vo.StorageStrategy;
import co.ohmygoods.global.file.repository.FileRepository;
import co.ohmygoods.global.file.service.dto.UploadFileRequest;
import co.ohmygoods.global.file.service.dto.UploadFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/*
    todo
        - getCloudStorageAccessURL 메서드와 upload 메서드 dto 분리
        - 반환값을 Optional 처리할지, File 도메인에서 예외 처리할지 선택
*/
@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final List<StorageService> storageServices;

    public Optional<URI> getCloudStorageAccessURL(UploadFileRequest request) {
        return findSupportStorageService(request.storageStrategy(), request.cloudStorageProvider())
                .map(StorageService::getCloudStorageAccessURL)
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

    public List<UploadFileResponse> upload(UploadFileRequest request) {
        Optional<StorageService> supportStorageService = findSupportStorageService(request.storageStrategy(), request.cloudStorageProvider());

        if (supportStorageService.isEmpty()) {
            throw FileException.INVALID_FILE;
        }

        List<UploadFileResponse> uploadResponse = supportStorageService.get().upload(request);

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

        return uploadResponse;
    }

    public List<URL> getUrls(DomainType domainType, Collection<String> domainIds) {
        List<File> files = fileRepository.findAllByDomainTypeAndDomainIds(domainType, domainIds);

        return files.isEmpty()
                ? Collections.emptyList()
                : findSupportStorageService(files.getFirst().getStorageStrategy(), files.getFirst().getCloudStorageProvider())
                        .map(service -> service.getFileAccessUrls(getStoragePath(files)))
                        .orElseGet(Collections::emptyList);
    }

    public List<InputStream> download(DomainType domainType, Collection<String> domainIds) {
        List<File> files = fileRepository.findAllByDomainTypeAndDomainIds(domainType, domainIds);

        return files.isEmpty()
                ? Collections.emptyList()
                : findSupportStorageService(files.getFirst().getStorageStrategy(), files.getFirst().getCloudStorageProvider())
                        .map(service -> service.downloadAll(getStoragePath(files)))
                        .orElseGet(Collections::emptyList);
    }

    public void delete(DomainType domainType, Collection<String> domainIds) {
        List<File> files = fileRepository.findAllByDomainTypeAndDomainIds(domainType, domainIds);

        if (!files.isEmpty()) {
            File first = files.getFirst();

            findSupportStorageService(first.getStorageStrategy(), first.getCloudStorageProvider())
                    .ifPresent(service -> service.deleteAll(getStoragePath(files)));

            fileRepository.deleteAll(files);
        }
    }

    private static List<String> getStoragePath(List<File> files) {
        return files.stream().map(File::getStoragePath).toList();
    }

    private Optional<StorageService> findSupportStorageService(StorageStrategy storageStrategy, CloudStorageProvider cloudStorageProvider) {
        return storageServices.stream()
                .filter(service -> service.canSupport(storageStrategy, cloudStorageProvider))
                .findFirst();
    }

}
