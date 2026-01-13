package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Meter;
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
    @Nullable
    private final Meter distance;
    private final Meter length;
    private final List<Coordinate> coordinates;

    public static CourseResponse from(Course course) {
        return from(course, null);
    }

    public static CourseResponse from(Course course, @Nullable Coordinate target) {
        return new CourseResponse(
                course.id(),
                course.name().value(),
                target != null ? course.distanceFrom(target) : null,
                course.length(),
                course.coordinates()
        );
    }

    public Optional<Meter> distance() {
        return Optional.ofNullable(distance);
    }
}
