package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

public class CourseReaderV0 implements Converter<Document, Course> {

    @Override
    public Course convert(Document source) {
        return new Course(
                source.getObjectId("_id").toHexString(),
                source.getString("name"),
                parseSegmentsToCoordinates(source.get("segments", Document.class))
        );
    }

    public List<Coordinate> parseSegmentsToCoordinates(Document source) {
        List<List<List<Double>>> segmentsData = (List<List<List<Double>>>) source.get("coordinates");

        List<Coordinate> coordinates = new ArrayList<>();
        for (List<List<Double>> segmentData : segmentsData) {
            if (!coordinates.isEmpty()) {
                coordinates.removeLast();
            }
            for (List<Double> coordinateData : segmentData) {
                coordinates.add(new Coordinate(coordinateData.get(1), coordinateData.get(0)));
            }
        }

        return coordinates;
    }
}
