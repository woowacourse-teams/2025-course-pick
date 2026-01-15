package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.List;

public abstract class CourseConverter {

    private static final CourseReaderV0 COURSE_READER_V0 = new CourseReaderV0();
    private static final CourseReaderV1 COURSE_READER_V1 = new CourseReaderV1();

    @WritingConverter
    public static class Writer implements Converter<Course, Document> {
        @Override
        public Document convert(Course source) {
            Document document = new Document();
            if (source.id() != null && !source.id().isBlank()) {
                document.put("_id", new ObjectId(source.id()));
            }
            document.put("name", source.name().value());
            document.put("coordinates", convertCoordinatesToGeoJson(source.coordinates()));
            document.put("length", source.length().value());
            document.put("schemaVersion", 1);
            return document;
        }

        private Document convertCoordinatesToGeoJson(List<Coordinate> coordinates) {
            List<List<Double>> coordinatesData = coordinates.stream()
                    .map(coordinate -> List.of(coordinate.longitude(), coordinate.latitude()))
                    .toList();

            Document document = new Document();
            document.put("type", "LineString");
            document.put("coordinates", coordinatesData);

            return document;
        }
    }

    @ReadingConverter
    public static class Reader implements Converter<Document, Course> {
        @Override
        public Course convert(Document source) {
            return switch (source.getInteger("schemaVersion")) {
                case 1 -> COURSE_READER_V1.convert(source);
                case null, default -> COURSE_READER_V0.convert(source);
            };
        }
    }
}
