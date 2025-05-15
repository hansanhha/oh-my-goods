package co.ohmygoods.auth.controller;

import co.ohmygoods.auth.account.service.AccountService;
import co.ohmygoods.auth.account.service.dto.AccountMetadataResponse;
import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "계정", description = "계정 관련 api")
@RequestMapping("/api/account")
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "계정 소유자의 계정 정보 조회")
    @ApiResponse(responseCode = "200", description = "계정 정보 반환",
            content = @Content(schema = @Schema(implementation = AccountMetadataWebResponse.class)))
    @GetMapping("/me")
    public ResponseEntity<AccountMetadataWebResponse> getMyAccountMetadata(@AuthenticationPrincipal AuthenticatedAccount account) {
        AccountMetadataResponse accountMetadata = accountService.getAccountMetadata(account.memberId());
        return ResponseEntity.ok(AccountMetadataWebResponse.from(accountMetadata));
    }

    public record AccountMetadataWebResponse(
            String memberId,
            String email,
            String nickname,
            String role,
            String oAuth2Provider,
            String profilePath,
            String profileName) {

        public static AccountMetadataWebResponse from(AccountMetadataResponse metadata) {
            return new AccountMetadataWebResponse(metadata.memberId(),
                    metadata.email(), metadata.nickname(), metadata.role().getRoleName(),
                    metadata.oauth2Provider().name().toLowerCase(), metadata.profileImgPath(),
                    metadata.profileImgName());
        }
    }
}
