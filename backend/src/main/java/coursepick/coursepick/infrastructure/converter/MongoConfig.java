package coursepick.coursepick.infrastructure.converter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                new SegmentListConverter.Reader(),
                new SegmentListConverter.Writer(),
                new CourseNameConverter.Reader(),
                new CourseNameConverter.Writer()
        ));
    }
}
