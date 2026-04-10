package coursepick.coursepick.infrastructure.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.infrastructure.compressor.DataCompressor;
import coursepick.coursepick.infrastructure.compressor.ZstdCompressor;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public abstract class CourseConverter {

    private static final DataCompressor DATA_COMPRESSOR = new ZstdCompressor();

    @WritingConverter
    public static class Writer implements Converter<Course, Document> {

        private final CourseWriter courseWriter;

        public Writer(ObjectMapper objectMapper) {
            this.courseWriter = new CourseWriter(DATA_COMPRESSOR, objectMapper);
        }

        @Override
        public Document convert(Course source) {
            return courseWriter.convert(source);
        }
    }

    @ReadingConverter
    public static class Reader implements Converter<Document, Course> {

        private final CourseReader courseReader;

        public Reader(ObjectMapper objectMapper) {
            this.courseReader = new CourseReader(DATA_COMPRESSOR, objectMapper);
        }

        @Override
        public Course convert(Document source) {
            return courseReader.convert(source);
        }
    }
}
