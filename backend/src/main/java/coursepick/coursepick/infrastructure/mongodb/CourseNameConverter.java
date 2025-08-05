package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.CourseName;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class CourseNameConverter {

    @WritingConverter
    public static class Writing implements Converter<CourseName, String> {
        @Override
        public String convert(CourseName source) {
            if (source == null || source.value() == null) {
                return null;
            }
            return source.value();
        }
    }

    @ReadingConverter
    public static class Reading implements Converter<String, CourseName> {
        @Override
        public CourseName convert(String source) {
            if (source == null) {
                // TODO : 적절하게 핸들링할것
                return null;
            }
            return new CourseName(source);
        }
    }
}
