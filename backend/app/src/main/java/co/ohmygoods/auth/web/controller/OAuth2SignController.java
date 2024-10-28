package co.ohmygoods.auth.web.controller;

import co.ohmygoods.auth.account.OAuth2SignService;
import co.ohmygoods.auth.jwt.JWTAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class OAuth2SignController {

    private final OAuth2SignService oAuth2SignService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal Authentication authentication) {
        var jwtAuthenticationToken = (JWTAuthenticationToken) authentication;
        var optionalOAuth2AccountDTO = oAuth2SignService.getOne(jwtAuthenticationToken.getName());

        return optionalOAuth2AccountDTO
                .<ResponseEntity<Map<String, Object>>>map(oAuth2AccountDTO -> ResponseEntity.ok(Map.of("userInfo", oAuth2AccountDTO)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal Authentication authentication) {
        var jwtAuthenticationToken = (JWTAuthenticationToken) authentication;
        oAuth2SignService.signOut(jwtAuthenticationToken.getPrincipal().tokenValue());

        return ResponseEntity.ok(Map.of("message", "succeed logout"));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> delete(@AuthenticationPrincipal Authentication authentication,
                                                      @RequestBody String emailToBeDelete) {
        var jwtAuthenticationToken = (JWTAuthenticationToken) authentication;
        var deleted = oAuth2SignService.deleteAccount(jwtAuthenticationToken.getPrincipal().tokenValue(), emailToBeDelete);

        return deleted
                ? ResponseEntity.ok(Map.of("result", true, "message", "deleted"))
                : ResponseEntity.badRequest().body(Map.of("result", false, "message", "deletion failed "));

    }
}
