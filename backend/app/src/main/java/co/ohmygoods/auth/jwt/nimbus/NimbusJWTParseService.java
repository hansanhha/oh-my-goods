package co.ohmygoods.auth.jwt.nimbus;

import co.ohmygoods.auth.jwt.JWTParseService;
import co.ohmygoods.auth.jwt.vo.JWTError;
import co.ohmygoods.auth.jwt.vo.ParsedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class NimbusJWTParseService implements JWTParseService<JWT> {

    @Override
    public ParsedJWT<JWT> parse(String token) {
        try {
            var parsed = JWTParser.parse(token);

            return ParsedJWT.success(parsed);
        } catch (Exception e) {
            if (e instanceof ParseException) {
                return ParsedJWT.failure(JWTError.MALFORMED);
            }
            return ParsedJWT.failure(JWTError.INVALID);
        }
    }
}
