package coursepick.coursepick.infrastructure.mongodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                new CourseNameConverter.Reading(),
                new CourseNameConverter.Writing(),
                new MeterConverter.Reading(),
                new MeterConverter.Writing(),
                new SegmentListConverter.Reading(),
                new SegmentListConverter.Writing()
        ));
    }
}
