package co.ohmygoods.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "ohmygoods api docs",
                version = "1.0.0"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "local")
        }
)
@Configuration
public class OpenAPIConfig {

    private static final String BEARER = "Bearer";
    private static final String JWT = "JWT";

    @Bean
    OpenAPI openAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList(JWT);

        Components components = new Components();
        components.addSecuritySchemes(JWT, getJwtScheme());
        components.addSchemas("ErrorResponse", getCommonErrorResponseSchema());

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private SecurityScheme getJwtScheme() {
        return new SecurityScheme()
                .name(JWT)
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER)
                .bearerFormat(JWT);
    }

    private Schema<?> getCommonErrorResponseSchema() {
        return new Schema<>()
                .type("object")
                .addProperty("success", new Schema<>().type("boolean").example("false"))
                .addProperty("problem_detail", new Schema<>().type("object")
                        .addProperty("status", new Schema<>().type("int").description("HTTP 상태 코드").example("404"))
                        .addProperty("title", new Schema<>().type("string").description("오류에 대한 간단한 설명").example("Not Found Account"))
                        .addProperty("detail",new Schema<>().type("string").description("오류에 대한 자세한 설명").example("아이디 '1'의 계정을 찾을 수 없습니다"))
                        .addProperty("type", new Schema<>().type("string").description("오류의 URI 참조").example("https://localhost:8080/accounts/1"))
                        .addProperty("instance", new Schema<>().type("string").description("오류 인스턴스에 대한 URI").example("/accounts/1"))
                );
    }
}
