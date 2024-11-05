package co.ohmygoods.auth.account.dto;

import co.ohmygoods.domain.oauth2.vo.OAuth2Vendor;

public record OAuth2SignUpRequest(String email, String oauth2MemberId, OAuth2Vendor vendor) {
}
