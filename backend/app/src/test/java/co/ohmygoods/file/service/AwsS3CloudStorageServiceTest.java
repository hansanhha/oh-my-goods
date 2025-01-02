package co.ohmygoods.file.service;

import co.ohmygoods.file.config.AwsS3CloudStorageProperties;
import co.ohmygoods.file.config.AwsS3Config;
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
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {AwsS3CloudStorageService.class, AwsS3Config.class})
@EnableConfigurationProperties(AwsS3CloudStorageProperties.class)
@TestPropertySource(locations = "classpath:application-test.yml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AwsS3CloudStorageServiceTest {

    @Autowired
    private AwsS3CloudStorageService awsS3CloudStorageService;

    @Test
    void 단일_이미지_업로드() throws IOException {
        UploadFileRequest uploadFileRequest = createSingleFileUploadRequest();

        List<UploadFileResponse> uploadResponse = awsS3CloudStorageService.upload(uploadFileRequest);

        assertThat(uploadResponse.size()).isEqualTo(uploadFileRequest.domainIdFileMap().size());
    }

    @Test
    void 다중_이미지_업로드() throws IOException {
        UploadFileRequest uploadFileRequest = createMultipleFileUploadRequest();

        List<UploadFileResponse> uploadResponse = awsS3CloudStorageService.upload(uploadFileRequest);

        assertThat(uploadResponse.size()).isEqualTo(uploadFileRequest.domainIdFileMap().size());
    }

    @Test
    void 단일_이미지_접근_url_반환() throws IOException {
        UploadFileRequest uploadFileRequest = createSingleFileUploadRequest();

        List<UploadFileResponse> uploadResponse = awsS3CloudStorageService.upload(uploadFileRequest);

        URL url = awsS3CloudStorageService.getFileAccessUrl(uploadResponse.getFirst().uploadedFilePath());

        System.out.println(url);
        assertThat(url).isNotNull();
    }

    @Test
    void 다중_이미지_접근_url_반환() throws IOException {
        UploadFileRequest uploadFileRequest = createMultipleFileUploadRequest();

        List<UploadFileResponse> uploadResponse = awsS3CloudStorageService.upload(uploadFileRequest);

        List<URL> urls = awsS3CloudStorageService.getFileAccessUrls(uploadResponse.stream()
                .map(UploadFileResponse::uploadedFilePath).toList());

        System.out.println(urls);
        assertThat(urls.size()).isEqualTo(uploadResponse.size());
    }

    @Test
    void 이미지_삭제() throws IOException {
        UploadFileRequest uploadFileRequest = createSingleFileUploadRequest();

        List<UploadFileResponse> uploadResponse = awsS3CloudStorageService.upload(uploadFileRequest);

        awsS3CloudStorageService.delete(uploadResponse.getFirst().uploadedFilePath());
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