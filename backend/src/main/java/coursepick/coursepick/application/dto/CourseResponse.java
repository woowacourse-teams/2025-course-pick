package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.Meter;
import coursepick.coursepick.domain.RoadType;

import java.util.List;

public record CourseResponse(
        Long id,
        String name,
        Meter distance,
        Meter length,
        RoadType roadType,
        double difficulty,
        List<SegmentResponse> segments
) {
    public static CourseResponse from(Course course, Coordinate target) {
        return new CourseResponse(
                course.id(),
                course.name(),
                course.distanceFrom(target),
                course.length(),
                course.roadType(),
                course.difficulty(),
                SegmentResponse.from(course.segments())
        );
    }
}
