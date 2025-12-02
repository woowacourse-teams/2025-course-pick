package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Slice;

import java.util.List;

public record CoursesResponse(
        List<CourseResponse> courses,
        boolean hasNext
) {
    public static CoursesResponse from(Slice<Course> courses, @Nullable Coordinate target) {
        List<CourseResponse> courseResponses = courses.stream()
                .map(course -> CourseResponse.from(course, target))
                .toList();

        return new CoursesResponse(courseResponses, courses.hasNext());
    }
}
