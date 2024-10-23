package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.account.model.Role;
import co.ohmygoods.auth.jwt.vo.JwtInfo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
public class JwtAuthenticationToken implements Authentication {

    private final JwtInfo jwtInfo;
    private boolean authenticated;
    private final Map<String, Object> details;

    public static JwtAuthenticationToken authenticated(JwtInfo jwtInfo, Map<String, Object> details) {
        return new JwtAuthenticationToken(jwtInfo, true, details);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Role.valueOf(jwtInfo.role().toUpperCase())
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
    public Object getPrincipal() {
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
