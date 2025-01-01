package co.ohmygoods.auth.jwt.service;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthenticatedUser principal;

    public JwtAuthenticationToken(AuthenticatedUser principal) {
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
