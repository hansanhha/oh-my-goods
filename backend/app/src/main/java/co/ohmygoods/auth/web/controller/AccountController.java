package co.ohmygoods.auth.web.controller;

import co.ohmygoods.auth.account.service.AccountService;
import co.ohmygoods.auth.jwt.service.JWTAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal Authentication authentication) {
        var jwtAuthenticationToken = (JWTAuthenticationToken) authentication;
        var optionalOAuth2AccountDTO = accountService.getOne(jwtAuthenticationToken.getName());

        return optionalOAuth2AccountDTO
                .<ResponseEntity<Map<String, Object>>>map(oAuth2AccountDTO -> ResponseEntity.ok(Map.of("userInfo", oAuth2AccountDTO)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
