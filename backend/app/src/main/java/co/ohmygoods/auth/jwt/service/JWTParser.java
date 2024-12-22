package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.service.dto.JWTParseResult;

public interface JWTParser<T> {

    JWTParseResult<T> parse(String token);
}
