package co.ohmygoods.auth.account.controller.dto;

import java.time.Instant;

public record ProfileNicknameUpdateResponse(
            String AccountMemberId,
            String AccountOriginalNickname,
            String AccountUpdateNickname,
            Instant updateAt) {
}