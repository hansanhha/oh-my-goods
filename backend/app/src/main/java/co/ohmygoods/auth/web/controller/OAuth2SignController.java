package co.ohmygoods.auth.web.controller;

import co.ohmygoods.auth.account.OAuth2SignService;
import co.ohmygoods.auth.jwt.JWTAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class OAuth2SignController {

    private final OAuth2SignService oAuth2SignService;

    @PostMapping("/account")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal Authentication authentication) {
        var jwtAuthenticationToken = (JWTAuthenticationToken) authentication;
        oAuth2SignService.signOut(jwtAuthenticationToken.getPrincipal().tokenValue());

        return ResponseEntity.ok(Map.of("message", "succeed logout"));
    }
}
