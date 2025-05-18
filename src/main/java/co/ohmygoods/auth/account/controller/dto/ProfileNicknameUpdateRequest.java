package co.ohmygoods.auth.account.controller.dto;

import jakarta.validation.constraints.Size;

public record ProfileNicknameUpdateRequest(
    
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
    String nickname) {
        
}


