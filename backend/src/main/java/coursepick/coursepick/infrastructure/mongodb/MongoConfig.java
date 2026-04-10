package coursepick.coursepick.infrastructure.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class MongoConfig {

    private final ObjectMapper objectMapper;

    public MongoConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions(ObjectMapper objectMapper) {
        return new MongoCustomConversions(List.of(
                new CourseConverter.Reader(objectMapper),
                new CourseConverter.Writer(objectMapper)
        ));
    }

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return builder -> builder
                .applyToConnectionPoolSettings(pool -> pool
                        .maxSize(30)
                        .minSize(10)
                        .maxWaitTime(5, TimeUnit.SECONDS)
                        .maxConnectionIdleTime(30, TimeUnit.SECONDS)
                )
                .applyToSocketSettings(socket -> socket
                        .connectTimeout(1, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                );
    }
}
