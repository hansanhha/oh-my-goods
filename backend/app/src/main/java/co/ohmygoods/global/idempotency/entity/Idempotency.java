package co.ohmygoods.global.idempotency.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("idempotency")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Idempotency {

    private String id;

    private int responseStatus;

    private String responseBody;

    private boolean isProcessing;

    private boolean isProcessed;

    public static Idempotency create(String id) {
        return new Idempotency(id, 0, null, true, false);
    }

    public void cacheResponse(int status, String responseBody) {
        this.isProcessed = true;
        this.isProcessing = false;
        this.responseStatus = status;
        this.responseBody = responseBody;
    }
}
