package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.*;

import java.util.List;

public record CourseResponse(
        Long id,
        String name,
        Meter distance,
        Meter length,
        RoadType roadType,
        Difficulty difficulty,
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
