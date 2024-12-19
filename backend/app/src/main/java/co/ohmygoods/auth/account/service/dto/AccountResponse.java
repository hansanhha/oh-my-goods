package co.ohmygoods.auth.account.service.dto;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.web.security.oauth2.OAuth2AuthorizationService;

public record AccountResponse(String email,
                              String nickname,
                              Role role,
                              OAuth2AuthorizationService.OAuth2Vendor oauth2Vendor,
                              String profileImgPath,
                              String profileImgName) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getEmail(),
                account.getNickname(),
                account.getRole(),
                account.getOauth2Vendor(),
                account.getProfileImagePath(),
                account.getProfileImageName());
    }
}
