package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class CourseResponse {

    private final Long id;
    private final String name;
    private final Meter distance;
    private final Meter length;
    private final RoadType roadType;
    private final Difficulty difficulty;
    private final List<SegmentResponse> segments;

    public static CourseResponse from(Course course, Coordinate target) {
        return new CourseResponse(
                course.id(),
                course.name().value(),
                course.distanceFrom(target),
                course.length(),
                course.roadType(),
                course.difficulty(),
                SegmentResponse.from(course.segments())
        );
    }

    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.id(),
                course.name().value(),
                null,
                course.length(),
                course.roadType(),
                course.difficulty(),
                SegmentResponse.from(course.segments())
        );
    }

    public Optional<Meter> distance() {
        return Optional.ofNullable(distance);
    }
}
