package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.course.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class CourseResponse {

    private final String id;
    private final String name;
    private final Meter distance;
    private final Meter length;
    private final RoadType roadType;
    private final InclineSummary inclineSummary;
    private final Difficulty difficulty;
    private final List<SegmentResponse> segments;

    public static CourseResponse from(Course course) {
        return from(course, null);
    }

    public static CourseResponse from(Course course, @Nullable Coordinate target) {
        return new CourseResponse(
                course.id(),
                course.name().value(),
                target != null ? course.distanceFrom(target) : null,
                course.length(),
                course.roadType(),
                course.inclineSummary(),
                course.difficulty(),
                SegmentResponse.from(course.segments())
        );
    }

    public Optional<Meter> distance() {
        return Optional.ofNullable(distance);
    }
}
