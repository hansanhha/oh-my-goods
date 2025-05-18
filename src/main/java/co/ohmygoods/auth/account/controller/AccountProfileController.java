package co.ohmygoods.auth.account.controller;

import co.ohmygoods.auth.account.controller.dto.AccountProfileResponse;
import co.ohmygoods.auth.account.service.AccountProfileService;
import co.ohmygoods.auth.account.service.dto.AccountProfile;
import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@Tag(name = "계정", description = "계정 프로필 관련 api")
@RequestMapping("/api/profile")
@RestController
@RequiredArgsConstructor
public class AccountProfileController {

    private final AccountProfileService profileService;

    @Operation(summary = "계정 소유자의 프로필 정보 조회")
    @ApiResponse(responseCode = "200", description = "프로필 정보 반환",
            content = @Content(schema = @Schema(implementation = AccountProfileResponse.class)))
    public ResponseEntity<AccountProfileResponse> getProfile(@AuthenticationPrincipal AuthenticatedAccount account) {
        AccountProfile profile = profileService.getAccountProfile(account.memberId());
        return ResponseEntity.ok(AccountProfileResponse.from(profile));
    }

}
