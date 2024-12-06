package co.ohmygoods.file.service.vo;

public enum StorageStrategy {

    /*
        프론트엔드에서 직접 파일 업로드
        일시적으로 허용된 클라우드 스토리지 접근 URL 주소를 프론트엔드에게 제공
     */
    PROVIDE_CLOUD_STORAGE_ACCESS_URL,

    // 백엔드에서 클라우드 스토리지 API를 사용해서 업로드
    CLOUD_STORAGE_API,

    LOCAL_FILE_SYSTEM;
}
