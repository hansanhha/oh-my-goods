package co.ohmygoods.auth.jwt.nimbus;

import co.ohmygoods.auth.jwt.JWTParser;
import co.ohmygoods.domain.jwt.vo.JWTError;
import co.ohmygoods.domain.jwt.vo.JWTParseResult;
import com.nimbusds.jwt.JWT;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class NimbusJWTParser implements JWTParser<JWT> {

    @Override
    public JWTParseResult<JWT> parse(String token) {
        try {
            var parsed = com.nimbusds.jwt.JWTParser.parse(token);

            return JWTParseResult.success(parsed);
        } catch (Exception e) {
            if (e instanceof ParseException) {
                return JWTParseResult.failure(JWTError.MALFORMED);
            }
            return JWTParseResult.failure(JWTError.INVALID);
        }
    }
}
