package co.ohmygoods.auth.jwt.service.nimbus;


import co.ohmygoods.auth.jwt.service.JWTParser;
import co.ohmygoods.auth.jwt.service.dto.JWTParseResult;

import com.nimbusds.jwt.JWT;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Qualifier("nimbusJoseJwtParser")
@Component
public class NimbusJoseJwtParser implements JWTParser<JWT> {

    @Override
    public JWTParseResult<JWT> parse(String token) {
        try {
            var parsed = com.nimbusds.jwt.JWTParser.parse(token);

            return JWTParseResult.success(parsed);
        } catch (Exception e) {
            return JWTParseResult.failure();
        }
    }
}
