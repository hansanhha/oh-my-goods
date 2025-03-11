package co.ohmygoods.global.idempotency;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
@ConfigurationProperties(prefix = "idempotency.lock")
public class IdempotencyLockProperties {

    private long waitTime;
    private long leaseTime;
    private TimeUnit timeUnit;
}
