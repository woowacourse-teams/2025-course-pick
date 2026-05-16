package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.course.Meter;

import java.util.List;

public record CourseWebResponse(
        String id,
        String name,
        Double distance,
        double length,
        List<CoordinateWebResponse> coordinates,
        String creatorId

) {
    public static List<CourseWebResponse> from(List<CourseResponse> courseResponses) {
        return courseResponses.stream()
                .map(CourseWebResponse::from)
                .toList();
    }

    public static CourseWebResponse from(CourseResponse courseResponse) {
        return new CourseWebResponse(
                courseResponse.id(),
                courseResponse.name(),
                courseResponse.distance()
                        .map(Meter::value)
                        .orElse(null),
                courseResponse.length().value(),
                CoordinateWebResponse.from(courseResponse.coordinates()),
                courseResponse.creatorId()
        );
    }
}
