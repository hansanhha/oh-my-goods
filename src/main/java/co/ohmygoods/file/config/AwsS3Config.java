package co.ohmygoods.file.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

    @Bean
    public S3Client s3Client() {
        Region SEOUL = Region.AP_NORTHEAST_2;

        return S3Client.builder()
                .region(SEOUL)
                .build();
    }
}
