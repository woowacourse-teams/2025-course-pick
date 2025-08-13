package coursepick.coursepick.presentation.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static coursepick.coursepick.application.exception.ErrorType.*;

@OpenAPIDefinition(info = @Info(title = "코스픽 API", version = "1.0.0"))
@Configuration
public class OpenApiConfig {

    private static final String TIMESTAMP = LocalDateTime.now().toString();

    @Bean
    public OpenApiCustomizer customize(
            @Value("${springdoc.dev-server-url:http://localhost:8080}") String devServerUrl,
            @Value("${springdoc.prod-server-url:http://localhost:8080}") String prodServerUrl
    ) {
        return openApi -> {
            openApi.setServers(List.of(
                    new Server()
                            .url("http://localhost:8080")
                            .description("로컬 서버"),
                    new Server()
                            .url(devServerUrl)
                            .description("개발 서버"),
                    new Server()
                            .url(prodServerUrl)
                            .description("운영 서버")
            ));

            Components components = openApi.getComponents();

            Example example = new Example()
                    .value(Map.of(
                            "message", INVALID_LATITUDE_RANGE.message("95"),
                            "timestamp", TIMESTAMP
                    ));
            components.addExamples(INVALID_LATITUDE_RANGE.name(), example);

            example = new Example()
                    .value(Map.of(
                            "message", INVALID_LONGITUDE_RANGE.message("185"),
                            "timestamp", TIMESTAMP
                    ));
            components.addExamples(INVALID_LONGITUDE_RANGE.name(), example);

            example = new Example()
                    .value(Map.of(
                            "message", NOT_EXIST_COURSE.message(99999),
                            "timestamp", TIMESTAMP
                    ));
            components.addExamples(NOT_EXIST_COURSE.name(), example);
        };
    }
}
