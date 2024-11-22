package co.ohmygoods.payment.vo;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    private int minimumPaymentPrice;
}
