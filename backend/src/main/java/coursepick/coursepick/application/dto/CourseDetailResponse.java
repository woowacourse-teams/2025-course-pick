package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Meter;

import java.util.List;

public record CourseDetailResponse(
        String id,
        String name,
        Meter length,
        List<Coordinate> coordinates,
        int reviewCount,
        List<ReviewResponse> reviews
) {
    public static CourseDetailResponse from(Course course) {
        return new CourseDetailResponse(
                course.id(),
                course.name().value(),
                course.length(),
                course.coordinates(),
                course.reviews().size(),
                ReviewResponse.from(course.reviews())
        );
    }
}
