package co.ohmygoods.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentServiceConfig {

    @Setter
    @Getter
    @ConfigurationProperties(prefix = "kakao.pay")
    public static class KakaoPayProperties {

        private String secretKey;

        private String cid;

        private String prepareUrl;

        private String approveUrl;

        private String approveRedirectUrl;

        private String cancelRedirectUrl;

        private String failRedirectUrl;
    }

    @Setter
    @Getter
    @ConfigurationProperties(prefix = "naver.pay")
    public static class NaverPayProperties {

    }

}
