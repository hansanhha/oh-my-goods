package co.ohmygoods.auth.account.service.dto;

import org.springframework.http.HttpStatusCode;

public record OAuth2AuthorizationResponse(boolean isSuccess,
                                          HttpStatusCode httpStatusCode,
                                          String oauth2ProviderErrorCode,
                                          String oauth2ProviderErrorMsg) {

    public static OAuth2AuthorizationResponse success(HttpStatusCode httpStatusCode) {
        return new OAuth2AuthorizationResponse(true, httpStatusCode, null, null);
    }

    public static OAuth2AuthorizationResponse fail(HttpStatusCode httpStatusCode, String oauth2ProviderErrorCode, String oauth2ProviderErrorMsg) {
        return new OAuth2AuthorizationResponse(false, httpStatusCode, oauth2ProviderErrorCode, oauth2ProviderErrorMsg);
    }
}
