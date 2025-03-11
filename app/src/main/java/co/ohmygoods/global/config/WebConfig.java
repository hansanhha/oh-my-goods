package co.ohmygoods.global.config;

import co.ohmygoods.global.logging.RequestProcessingLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private static final String ALL_REQUEST = "/**";
    private static final String STATIC_RESOURCE_ALL_REQUEST = "/static/**";
    private static final String ERROR_REDIRECT_REQUEST = "/error";

    private final RequestProcessingLoggingInterceptor requestProcessingLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestProcessingLoggingInterceptor)
                .addPathPatterns(ALL_REQUEST)
                .excludePathPatterns(STATIC_RESOURCE_ALL_REQUEST, ERROR_REDIRECT_REQUEST);
    }

}
