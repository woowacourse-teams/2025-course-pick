package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;

import java.util.List;

public record CourseResponse(
        String type,
        GeometryResponse geometry,
        PropertiesResponse properties
) {
    record GeometryResponse(
            String type,
            List<List<Double>> coordinates
    ) {
        public static GeometryResponse from(List<Coordinate> coordinates) {
            return new GeometryResponse(
                    "LineString",
                    coordinates.stream()
                            .map(co -> List.of(co.latitude(), co.longitude()))
                            .toList()
            );
        }
    }

    record PropertiesResponse(
            String name,
            double distance,
            double length
    ) {
        public static PropertiesResponse from(Course course, double latitude, double longitude) {
            return new PropertiesResponse(
                    course.name(),
                    course.minDistanceFrom(new Coordinate(latitude, longitude)),
                    course.length()
            );
        }
    }

    public static CourseResponse from(Course course, double latitude, double longitude) {
        return new CourseResponse(
                "Feature",
                GeometryResponse.from(course.coordinates()),
                PropertiesResponse.from(course, latitude, longitude)
        );
    }

    public static List<CourseResponse> from(List<Course> courses, double latitude, double longitude) {
        return courses.stream()
                .map(co -> CourseResponse.from(co, latitude, longitude))
                .toList();
    }
}
