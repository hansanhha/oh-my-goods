package co.ohmygoods.global.idempotency.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Idempotency implements Serializable {

    private String id;

    private String httpMethod;

    private String servletPath;

    private String accessToken;

    private int responseStatus;

    private String responseBody;

    private boolean isProcessing;

    public static Idempotency create(String id, String httpMethod, String servletPath, String accessToken) {
        return new Idempotency(id, httpMethod, servletPath, accessToken, 0, null, true);
    }

    public void cacheResponse(int status, String body) {
        isProcessing = false;
        responseStatus = status;
        responseBody = body;
    }

    public void cacheUnknownError() {
        isProcessing = false;
        responseStatus = 500;
        responseBody = "unknown error";
    }
}
