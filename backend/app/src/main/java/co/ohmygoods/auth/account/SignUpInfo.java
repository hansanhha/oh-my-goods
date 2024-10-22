package co.ohmygoods.auth.account;

import co.ohmygoods.auth.account.model.OAuth2Vendor;

public record SignUpInfo(String email, String oauth2MemberId, OAuth2Vendor vendor) {
}
