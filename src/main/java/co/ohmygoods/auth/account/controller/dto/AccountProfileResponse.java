package co.ohmygoods.auth.account.controller.dto;

import co.ohmygoods.auth.account.service.dto.AccountProfile;

public record AccountProfileResponse(
            String AccountMemberId,
            String AccountEmail,
            String AccountNickname,
            String AccountRole,
            String AccountOAuth2Provider,
            String AccountProfilePath,
            String AccountProfileName) {

    public static AccountProfileResponse from(AccountProfile profile) {
        return new AccountProfileResponse(
            profile.memberId(),
            profile.email(), 
            profile.nickname(),
            profile.role().toString(),
            profile.oauth2Provider().toString(),
            profile.profileImgPath(),
            profile.nickname());
    }
}
