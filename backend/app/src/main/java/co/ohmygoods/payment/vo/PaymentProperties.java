package co.ohmygoods.payment.vo;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    private int minimumPaymentPrice;

    @Getter
    @ConfigurationProperties(prefix = "kakao.pay")
    public static class KakaoPayProperties {

        private String secretKey;

        private String cid;

        private String preparationRequestUrl;

        private String approvalRequestUrl;

        private String approvalRedirectUrl;

        private String cancelRedirectUrl;

        private String failRedirectUrl;
    }

    @ConfigurationProperties(prefix = "naver.pay")
    public static class NaverPayProperties {

    }
}
