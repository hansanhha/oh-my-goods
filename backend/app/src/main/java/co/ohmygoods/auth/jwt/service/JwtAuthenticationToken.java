package co.ohmygoods.auth.jwt.service;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthenticatedAccount principal;

    public JwtAuthenticationToken(AuthenticatedAccount principal) {
        super(principal.role().toGrantedAuthorities());
        this.setAuthenticated(true);
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
