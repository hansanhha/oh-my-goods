package co.ohmygoods.auth.account.service.dto;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;

public record AccountResponse(String email,
                              String nickname,
                              Role role,
                              OAuth2Provider oauth2Provider,
                              String profileImgPath,
                              String profileImgName) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getEmail(),
                account.getNickname(),
                account.getRole(),
                account.getOauth2Provider(),
                account.getProfileImagePath(),
                account.getProfileImageName());
    }
}
