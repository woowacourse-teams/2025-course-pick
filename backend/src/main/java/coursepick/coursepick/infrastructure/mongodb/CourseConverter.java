package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.infrastructure.compressor.DataCompressor;
import coursepick.coursepick.infrastructure.compressor.ZstdCompressor;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public abstract class CourseConverter {

    private static final DataCompressor DATA_COMPRESSOR = new ZstdCompressor();
    private static final CourseReader COURSE_READER = new CourseReader(DATA_COMPRESSOR);
    private static final CourseWriter COURSE_WRITER = new CourseWriter(DATA_COMPRESSOR);

    @WritingConverter
    public static class Writer implements Converter<Course, Document> {
        @Override
        public Document convert(Course source) {
            return COURSE_WRITER.convert(source);
        }
    }

//     document.put("schemaVersion", 2);
//
//            if(source.creator().id() != null && !source.creator().id().isBlank()) {
//        document.put("creator", source.creator().id());
//    }

    @ReadingConverter
    public static class Reader implements Converter<Document, Course> {
        @Override
        public Course convert(Document source) {
            return COURSE_READER.convert(source);
        }
    }
}
