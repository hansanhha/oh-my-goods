package co.ohmygoods.auth.security;


import co.ohmygoods.auth.account.model.vo.AuthenticatedAccount;

import org.springframework.security.authentication.AbstractAuthenticationToken;


public class JWTAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthenticatedAccount principal;

    public JWTAuthenticationToken(AuthenticatedAccount principal) {
        super(principal.role().toGrantedAuthorities());
        this.setAuthenticated(true);
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public AuthenticatedAccount getPrincipal() {
        return principal;
    }
}
