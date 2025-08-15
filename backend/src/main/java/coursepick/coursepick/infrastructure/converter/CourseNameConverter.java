package coursepick.coursepick.infrastructure.converter;

import coursepick.coursepick.domain.CourseName;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class CourseNameConverter {

    @WritingConverter
    public static class Writer implements Converter<CourseName, String> {
        @Override
        public String convert(CourseName source) {
            return source.value();
        }
    }

    @ReadingConverter
    public static class Reader implements Converter<String, CourseName> {
        @Override
        public CourseName convert(String source) {
            return new CourseName(source);
        }
    }
}
