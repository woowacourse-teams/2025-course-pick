package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.InclineSummary;
import coursepick.coursepick.domain.course.RoadType;

import java.util.List;

public record AdminCourseWebResponse(
        String id,
        String name,
        double length,
        RoadType roadType,
        InclineSummary inclineSummary,
        String difficulty,
        List<AdminSegmentWebResponse> segments
) {
    public static List<AdminCourseWebResponse> from(List<Course> courseResponses) {
        return courseResponses.stream()
                .map(course -> new AdminCourseWebResponse(
                        course.id(),
                        course.name().value(),
                        course.length().value(),
                        course.roadType(),
                        course.inclineSummary(),
                        course.difficulty().name(),
                        AdminSegmentWebResponse.from(course.segments())
                )).toList();
    }

    public static AdminCourseWebResponse from(Course course) {
        return new AdminCourseWebResponse(
                course.id(),
                course.name().value(),
                course.length().value(),
                course.roadType(),
                course.inclineSummary(),
                course.difficulty().name(),
                AdminSegmentWebResponse.from(course.segments())
        );
    }
}
