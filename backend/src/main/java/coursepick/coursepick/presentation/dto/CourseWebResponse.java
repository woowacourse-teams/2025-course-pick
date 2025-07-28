package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.RoadType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CourseWebResponse(
        @Schema(example = "1")
        long id,
        @Schema(example = "석촌호수")
        String name,
        @Schema(example = "200.123")
        double distance,
        @Schema(example = "2146.123")
        double length,
        @Schema(example = "트랙")
        RoadType roadType,
        @Schema(example = "1.235")
        double difficulty,
        List<SegmentWebResponse> segments
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
                        SegmentWebResponse.from(courseResponse.segments())
                )).toList();
    }
}
