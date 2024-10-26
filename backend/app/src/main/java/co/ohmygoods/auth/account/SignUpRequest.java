package co.ohmygoods.auth.account;

import co.ohmygoods.auth.oauth2.vo.OAuth2Vendor;

public record SignUpRequest(String email, String oauth2MemberId, OAuth2Vendor vendor) {
}
