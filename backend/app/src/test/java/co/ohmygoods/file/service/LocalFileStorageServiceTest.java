package co.ohmygoods.file.service;

import co.ohmygoods.file.config.LocalFileStorageProperties;
import co.ohmygoods.file.model.vo.DomainType;
import co.ohmygoods.file.service.dto.UploadFileRequest;
import co.ohmygoods.file.service.dto.UploadFileResponse;
import com.google.common.io.Files;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = {LocalFileStorageService.class})
@EnableConfigurationProperties(LocalFileStorageProperties.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LocalFileStorageServiceTest {

    @Autowired
    private LocalFileStorageService fileStorageService;

    @Test
    void 단일_이미지_파일_저장() throws IOException {
        UploadFileRequest uploadFileRequest = createSingleFileUploadRequest();

        List<UploadFileResponse> uploadResponse = fileStorageService.upload(uploadFileRequest);

        assertThat(uploadResponse.size()).isEqualTo(uploadFileRequest.domainIdFileMap().size());
    }

    @Test
    void 복수_이미지_파일_저장() throws IOException {
        UploadFileRequest uploadFileRequest = createMultipleFileUploadRequest();

        List<UploadFileResponse> uploadResponse = fileStorageService.upload(uploadFileRequest);

        assertThat(uploadResponse.size()).isEqualTo(uploadFileRequest.domainIdFileMap().size());
    }

    @Test
    void 복수_이미지_파일_조회() throws IOException {
        UploadFileRequest uploadFileRequest = createMultipleFileUploadRequest();

        List<UploadFileResponse> uploadResponse = fileStorageService.upload(uploadFileRequest);

        Collection<InputStream> downloadedAll = fileStorageService.downloadAll(uploadResponse.stream()
                .map(UploadFileResponse::uploadedFilePath).toList());

        assertThat(downloadedAll.size()).isEqualTo(uploadFileRequest.domainIdFileMap().size());
    }

    @Test
    void 이미지_삭제() throws IOException {
        UploadFileRequest uploadFileRequest = createMultipleFileUploadRequest();

        List<UploadFileResponse> uploadResponse = fileStorageService.upload(uploadFileRequest);

        fileStorageService.deleteAll(uploadResponse.stream()
                .map(UploadFileResponse::uploadedFilePath).toList());

        Collection<InputStream> downloadedAll = fileStorageService.downloadAll(uploadResponse.stream()
                .map(UploadFileResponse::uploadedFilePath).toList());

        assertThat(downloadedAll.size()).isZero();
    }

    private UploadFileRequest createSingleFileUploadRequest() throws IOException {
        ClassLoader classLoader = LocalFileStorageServiceTest.class.getClassLoader();
        File imageFile = new File(classLoader.getResource("images/testImg1.jpg").getFile());
        MockMultipartFile mockMultipartFile = new MockMultipartFile("testFile", imageFile.getName(),
                MediaType.IMAGE_JPEG_VALUE, Files.toByteArray(imageFile));

        return UploadFileRequest.useLocalFileSystem(
                "uploaderEmail@memberId.com", DomainType.SHOP,
                Map.of(String.valueOf(1L), mockMultipartFile));
    }

    private UploadFileRequest createMultipleFileUploadRequest() throws IOException {
        ClassLoader classLoader = LocalFileStorageServiceTest.class.getClassLoader();
        File imageFile = new File(classLoader.getResource("images/testImg1.jpg").getFile());
        File imageFile2 = new File(classLoader.getResource("images/testImg2.jpg").getFile());
        File imageFile3 = new File(classLoader.getResource("images/testImg3.jpg").getFile());
        MockMultipartFile mockMultipartFile = new MockMultipartFile("testFile", imageFile.getName(),
                MediaType.IMAGE_JPEG_VALUE, Files.toByteArray(imageFile));
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("testFile2", imageFile2.getName(),
                MediaType.IMAGE_JPEG_VALUE, Files.toByteArray(imageFile2));
        MockMultipartFile mockMultipartFile3 = new MockMultipartFile("testFile3", imageFile3.getName(),
                MediaType.IMAGE_JPEG_VALUE, Files.toByteArray(imageFile3));

        return UploadFileRequest.useLocalFileSystem(
                "uploaderEmail@memberId.com", DomainType.SHOP,
                Map.of(String.valueOf(1L), mockMultipartFile, String.valueOf(2L),
                        mockMultipartFile2, String.valueOf(3L), mockMultipartFile3));
    }

}