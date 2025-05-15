package co.ohmygoods.file.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("storage.local")
public class LocalFileStorageProperties {

    private String baseDirectory;
}
