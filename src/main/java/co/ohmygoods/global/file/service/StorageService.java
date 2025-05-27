package co.ohmygoods.global.file.service;

import co.ohmygoods.global.file.service.dto.UploadFileRequest;
import co.ohmygoods.global.file.service.dto.UploadFileResponse;
import co.ohmygoods.global.file.model.vo.CloudStorageProvider;
import co.ohmygoods.global.file.model.vo.StorageStrategy;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/*
    todo
        파일 업로드 비동기/병렬 처리
 */
public interface StorageService {

    /**
     *
     * @param request 파일 저장 dto
     * @return 파일 저장 경로(path)
     */
    List<UploadFileResponse> upload(UploadFileRequest request);

    void delete(String path);

    default void deleteAll(List<String> paths) {
        paths.forEach(this::delete);
    }

    default URL getFileAccessUrl(String path) {
        throw new UnsupportedOperationException();
    }

    default List<URL> getFileAccessUrls(List<String> paths) {
        return paths.stream().map(this::getFileAccessUrl).toList();
    }

    default InputStream download(String path) {
        throw new UnsupportedOperationException();
    }

    default List<InputStream> downloadAll(List<String> paths) {
        return paths.stream().map(this::download).toList();
    }

    default Optional<URI> getCloudStorageAccessURL() {
        return Optional.empty();
    }

    boolean canSupport(StorageStrategy storageStrategy, CloudStorageProvider cloudStorageProvider);

}
