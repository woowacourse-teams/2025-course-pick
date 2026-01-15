package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Course;

import java.util.List;

public record AdminCourseWebResponse(
        String id,
        String name,
        double length,
        List<AdminCoordinateWebResponse> coordinates
) {
    public static List<AdminCourseWebResponse> from(List<Course> courseResponses) {
        return courseResponses.stream()
                .map(AdminCourseWebResponse::from)
                .toList();
    }

    public static AdminCourseWebResponse from(Course course) {
        return new AdminCourseWebResponse(
                course.id(),
                course.name().value(),
                course.length().value(),
                AdminCoordinateWebResponse.from(course.coordinates())
        );
    }
}
