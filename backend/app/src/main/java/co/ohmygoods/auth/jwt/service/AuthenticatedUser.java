package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.account.model.vo.Role;
import org.springframework.security.core.AuthenticatedPrincipal;

public record AuthenticatedUser(
        String memberId,
        Role role) implements AuthenticatedPrincipal {

    @Override
    public String getName() {
        return memberId;
    }
}
