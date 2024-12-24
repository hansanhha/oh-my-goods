package co.ohmygoods.auth.jwt.service.nimbus;

import co.ohmygoods.auth.jwt.service.JWTParser;
import co.ohmygoods.auth.jwt.model.vo.JWTError;
import co.ohmygoods.auth.jwt.service.dto.JwtParseResult;
import com.nimbusds.jwt.JWT;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class NimbusJoseJwtParser implements JWTParser<JWT> {

    @Override
    public JwtParseResult<JWT> parse(String token) {
        try {
            var parsed = com.nimbusds.jwt.JWTParser.parse(token);

            return JwtParseResult.success(parsed);
        } catch (Exception e) {
            if (e instanceof ParseException) {
                return JwtParseResult.failure(JWTError.MALFORMED);
            }
            return JwtParseResult.failure(JWTError.INVALID);
        }
    }
}
