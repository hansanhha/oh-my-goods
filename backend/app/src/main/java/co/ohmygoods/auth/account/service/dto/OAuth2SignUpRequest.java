package co.ohmygoods.auth.account.service.dto;

import co.ohmygoods.auth.web.security.oauth2.OAuth2AuthorizationService;

public record OAuth2SignUpRequest(String email,
                                  String oauth2MemberId,
                                  OAuth2AuthorizationService.OAuth2Vendor vendor) {
}
