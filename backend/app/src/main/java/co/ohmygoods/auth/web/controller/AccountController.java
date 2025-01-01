package co.ohmygoods.auth.web.controller;

import co.ohmygoods.auth.account.service.AccountService;
import co.ohmygoods.auth.account.service.dto.AccountMetadataResponse;
import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/account")
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me")
    public AccountMetadataResponse getMyAccountMetadata(@AuthenticationPrincipal AuthenticatedAccount account) {
        return accountService.getAccountMetadata(account.memberId());
    }
}
