package co.ohmygoods.account.service;

import co.ohmygoods.account.domain.OAuth2Vendor;

public record SignUpInfo(String email, String oauth2MemberId, OAuth2Vendor vendor) {
}
