package co.ohmygoods.global.lock;

import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @see <a href=https://helloworld.kurly.com/blog/distributed-redisson-lock>코드 참고</a>
 */
public class LockGeyGenerator {

    private LockGeyGenerator() {}

    public static String generate(String prefix, String[] parameterName, Object[] arguments, String key) {
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterName.length; i++) {
            context.setVariable(parameterName[i], arguments[i]);
        }

        return parser.parseExpression(key).getValue(context, String.class);
    }
}
