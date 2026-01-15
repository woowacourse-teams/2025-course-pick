package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseName;
import coursepick.coursepick.domain.course.Meter;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

public class CourseReaderV1 implements Converter<Document, Course> {

    @Override
    public Course convert(Document source) {
        return new Course(
                source.getObjectId("_id").toHexString(),
                new CourseName(source.getString("name")),
                parseCoordinates(source.get("coordinates", Document.class)),
                new Meter(source.getDouble("length"))
        );
    }

    public List<Coordinate> parseCoordinates(Document source) {
        List<List<Double>> coordinatesData = (List<List<Double>>) source.get("coordinates");

        return coordinatesData.stream()
                .map(coordinateData -> new Coordinate(coordinateData.get(1), coordinateData.get(0)))
                .toList();
    }
}
