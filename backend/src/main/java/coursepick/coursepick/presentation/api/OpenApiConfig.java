package coursepick.coursepick.presentation.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static coursepick.coursepick.application.exception.ErrorType.values;

@Profile("!prod")
@OpenAPIDefinition(info = @Info(title = "코스픽 API"))
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
