package co.ohmygoods.auth.web.controller;

import co.ohmygoods.auth.account.service.OAuth2AccountService;
import co.ohmygoods.auth.jwt.service.JWTAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuth2SignController {

    private final OAuth2AccountService OAuth2AccountService;

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(@AuthenticationPrincipal Authentication authentication) {
        var jwtAuthenticationToken = (JWTAuthenticationToken) authentication;
        var principal = jwtAuthenticationToken.getPrincipal();

        var jwts = OAuth2AccountService.reissueJWT(principal.tokenValue());
        return ResponseEntity.ok(Map.of("message", "succeed reissue tokens", "tokens", jwts));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal Authentication authentication) {
        var jwtAuthenticationToken = (JWTAuthenticationToken) authentication;
        OAuth2AccountService.signOut(jwtAuthenticationToken.getPrincipal().tokenValue());

        return ResponseEntity.ok(Map.of("message", "succeed logout"));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> delete(@AuthenticationPrincipal Authentication authentication,
                                                      @RequestBody String emailToBeDelete) {
        var jwtAuthenticationToken = (JWTAuthenticationToken) authentication;
        var deleted = OAuth2AccountService.deleteAccount(jwtAuthenticationToken.getPrincipal().tokenValue(), emailToBeDelete);

        return deleted
                ? ResponseEntity.ok(Map.of("result", true, "message", "deleted"))
                : ResponseEntity.badRequest().body(Map.of("result", false, "message", "deletion failed "));

    }
}
