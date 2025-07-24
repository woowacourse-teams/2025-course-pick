package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.RoadType;

import java.util.List;

public record CourseWebResponse(
        long id,
        String name,
        double distance,
        double length,
        RoadType roadType,
        double difficulty,
        List<CoordinateWebResponse> coordinates
) {
    public static List<CourseWebResponse> from(List<CourseResponse> courseResponses) {
        return courseResponses.stream()
                .map(courseResponse -> new CourseWebResponse(
                        courseResponse.id(),
                        courseResponse.name(),
                        courseResponse.distance().value(),
                        courseResponse.length().value(),
                        courseResponse.roadType(),
                        courseResponse.difficulty(),
                        CoordinateWebResponse.from(courseResponse.coordinates())
                )).toList();
    }
}
