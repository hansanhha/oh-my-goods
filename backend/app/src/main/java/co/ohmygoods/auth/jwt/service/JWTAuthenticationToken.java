package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.model.vo.JWTInfo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
public class JWTAuthenticationToken implements Authentication {

    private final JWTInfo jwtInfo;
    private boolean authenticated;
    private final Map<String, Object> details;

    public static JWTAuthenticationToken authenticated(JWTInfo jwtInfo, Map<String, Object> details) {
        return new JWTAuthenticationToken(jwtInfo, true, details);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return jwtInfo.role()
                .getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .toList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public JWTInfo getPrincipal() {
        return jwtInfo;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return jwtInfo.subject();
    }
}
