package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.jwt.vo.ParsedJWT;

public interface JWTParseService<T> {

    ParsedJWT<T> parse(String token);
}
