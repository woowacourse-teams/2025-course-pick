package coursepick.coursepick.infrastructure;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            .withZone(ZoneId.of("Asia/Seoul"));

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.timeZone(TimeZone.getTimeZone("Asia/Seoul"));
            
            builder.serializerByType(Instant.class, new JsonSerializer<Instant>() {
                @Override
                public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                    jsonGenerator.writeString(FORMATTER.format(instant));
                }
            });
            
            builder.deserializerByType(Instant.class, new JsonDeserializer<Instant>() {
                @Override
                public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                    return Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(jsonParser.getText()));
                }
            });
        };
    }
}
