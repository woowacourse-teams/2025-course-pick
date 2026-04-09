package coursepick.coursepick.presentation.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.examples.Example;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static coursepick.coursepick.application.exception.ErrorType.values;

@Profile("!prod")
@OpenAPIDefinition(info = @Info(title = "코스픽 API"))
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT 토큰을 입력하세요. 'Bearer ' 접두사는 자동으로 추가됩니다."
)
@Configuration
public class OpenApiConfig {

    private static final String TIMESTAMP = LocalDateTime.now().toString();

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/v1/**")
                .build();
    }

    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
                .group("v2")
                .pathsToMatch("/v2/**")
                .build();
    }

    @Bean
    public OpenApiCustomizer customize() {
        return openApi -> {
            Components components = openApi.getComponents();

            Arrays.stream(values()).forEach(
                    errorType -> {
                        Example example = new Example()
                                .value(Map.of(
                                        "message", errorType.message("{}", "{}", "{}"),
                                        "timestamp", TIMESTAMP
                                ));
                        components.addExamples(errorType.name(), example);

                    }
            );
        };
    }
}
