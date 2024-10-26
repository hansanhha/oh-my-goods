package co.ohmygoods.auth.account;

import co.ohmygoods.auth.jwt.vo.JWTs;

import java.util.Optional;

public interface SignService {

    Optional<Long> findIdByEmail(String email);

    Long signUp(SignUpRequest t);

    JWTs signIn(String email);

    void signOut(String accessToken);

    void deleteAccount(String email);
}
