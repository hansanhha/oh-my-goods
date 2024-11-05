package co.ohmygoods.auth.account.dto;

import co.ohmygoods.domain.account.entity.OAuth2Account;
import co.ohmygoods.domain.account.vo.Role;
import co.ohmygoods.domain.oauth2.vo.OAuth2Vendor;

public record OAuth2AccountDTO(String email,
                               String nickname,
                               Role role,
                               OAuth2Vendor oauth2Vendor,
                               String profileImgPath,
                               String profileImgName) {

    public static OAuth2AccountDTO from(OAuth2Account oAuth2Account) {
        return new OAuth2AccountDTO(
                oAuth2Account.getEmail(),
                oAuth2Account.getNickname(),
                oAuth2Account.getRole(),
                oAuth2Account.getOauth2Vendor(),
                oAuth2Account.getProfileImagePath(),
                oAuth2Account.getProfileImageName());
    }
}
