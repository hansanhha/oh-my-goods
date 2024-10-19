package co.ohmygoods.account.info.service;

import co.ohmygoods.account.model.OAuth2Vendor;

public record SignUpInfo(String email, String oauth2MemberId, OAuth2Vendor vendor) {
}
