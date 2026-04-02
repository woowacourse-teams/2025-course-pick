package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseName;
import coursepick.coursepick.domain.course.Meter;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

public class CourseReader implements Converter<Document, Course> {

    @Override
    public Course convert(Document source) {
        List<Coordinate> coordinates = parseCoordinates(source.get("coordinates", Document.class));
        List<Coordinate> simplifiedCoordinates;

        if (source.containsKey("simplifiedCoordinates")) {
            simplifiedCoordinates = parseCoordinates(source.get("simplifiedCoordinates", Document.class));
        } else {
            simplifiedCoordinates = coordinates; // Or use Course.simplifyCoordinates logic
        }

        return new Course(
                source.getObjectId("_id").toHexString(),
                new CourseName(source.getString("name")),
                coordinates,
                simplifiedCoordinates,
                new Meter(source.getDouble("length"))
        );
    }

    public List<Coordinate> parseCoordinates(Document source) {
        List<List<Object>> coordinatesData = (List<List<Object>>) source.get("coordinates");

        return coordinatesData.stream()
                .map(coordinateData -> new Coordinate(
                        ((Number) coordinateData.get(1)).doubleValue(),
                        ((Number) coordinateData.get(0)).doubleValue()
                ))
                .toList();
    }
}
