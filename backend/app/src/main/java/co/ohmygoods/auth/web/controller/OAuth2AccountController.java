package co.ohmygoods.auth.web.controller;

import co.ohmygoods.auth.account.OAuth2SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuth2AccountController {

    private final OAuth2SignService oAuth2SignService;


}
