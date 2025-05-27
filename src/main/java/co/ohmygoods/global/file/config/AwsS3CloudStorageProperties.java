package co.ohmygoods.global.file.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("storage.aws-s3")
public class AwsS3CloudStorageProperties {

    private String bucketName;

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName.toLowerCase();
    }
}
