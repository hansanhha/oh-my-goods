package co.ohmygoods.auth.account.service.dto;

import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;

import java.util.Map;

public record OAuth2SignUpRequest(String email,
                                  Map<String, Object> attributes,
                                  String oauth2MemberId,
                                  OAuth2Provider oAuth2Provider) {
}
