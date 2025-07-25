package coursepick.coursepick.presentation.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.examples.Example;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Map;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_LATITUDE_RANGE;
import static coursepick.coursepick.application.exception.ErrorType.INVALID_LONGITUDE_RANGE;

@OpenAPIDefinition(
        info = @Info(title = "코스픽 API", version = "1.0.0"),
        servers = @Server(url = "http://54.180.213.93", description = "개발 서버")
)
@Configuration
public class OpenApiConfig {

    private static final String TIMESTAMP = LocalDateTime.now().toString();

    @Bean
    public OpenApiCustomizer exampleInjector() {
        return openApi -> {
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
        };
    }
}
