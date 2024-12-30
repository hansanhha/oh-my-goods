package co.ohmygoods.auth.jwt.service.nimbus;

import co.ohmygoods.auth.jwt.service.JwtParser;
import co.ohmygoods.auth.jwt.service.dto.JwtParseResult;
import com.nimbusds.jwt.JWT;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Qualifier("nimbusJoseJwtParser")
@Component
public class NimbusJoseJwtParser implements JwtParser<JWT> {

    @Override
    public JwtParseResult<JWT> parse(String token) {
        try {
            var parsed = com.nimbusds.jwt.JWTParser.parse(token);

            return JwtParseResult.success(parsed);
        } catch (Exception e) {
            return JwtParseResult.failure();
        }
    }
}
