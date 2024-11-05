package co.ohmygoods.auth.jwt;

import co.ohmygoods.domain.jwt.vo.JWTParseResult;

public interface JWTParser<T> {

    JWTParseResult<T> parse(String token);
}
