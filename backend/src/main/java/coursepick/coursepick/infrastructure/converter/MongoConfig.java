package coursepick.coursepick.infrastructure.converter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions(SegmentConverter segmentConverter) {
        return new MongoCustomConversions(List.of(segmentConverter));
    }
}
