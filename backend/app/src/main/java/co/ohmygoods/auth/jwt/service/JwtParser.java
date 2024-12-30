package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.service.dto.JwtParseResult;

public interface JwtParser<T> {

    JwtParseResult<T> parse(String token);
}
