package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.Meter;

import java.util.List;

public record CourseResponse(
        String name,
        List<Coordinate> coordinates,
        Meter meter,
        Meter length
) {
    public static CourseResponse from(Course course, Coordinate target) {
        return new CourseResponse(
                course.name(),
                course.coordinates(),
                course.minDistanceFrom(target),
                course.length()
        );
    }
}
