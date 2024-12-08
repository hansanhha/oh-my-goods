package co.ohmygoods.file.service;

import co.ohmygoods.file.service.dto.UploadFileRequest;
import co.ohmygoods.file.service.dto.UploadFileResponse;
import co.ohmygoods.file.model.vo.CloudStorageProvider;
import co.ohmygoods.file.model.vo.StorageStrategy;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FileStorageService {

    /**
     *
     * @param request 파일 저장 dto
     * @return 파일 저장 경로(path)
     */
    Collection<UploadFileResponse> upload(UploadFileRequest request);

    Optional<InputStream> download(String path);

    void delete(String path);

    default Optional<URI> getCloudStorageAccessURL() {
        return Optional.empty();
    }

    default Collection<Optional<InputStream>> downloadAll(List<String> paths) {
        return paths.stream().map(this::download).toList();
    }

    default void deleteAll(List<String> paths) {
        paths.forEach(this::delete);
    }

    boolean canSupport(StorageStrategy storageStrategy, CloudStorageProvider cloudStorageProvider);

}
