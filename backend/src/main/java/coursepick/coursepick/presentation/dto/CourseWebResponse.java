package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.InclineSummary;
import coursepick.coursepick.domain.Meter;
import coursepick.coursepick.domain.RoadType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CourseWebResponse(
        @Schema(example = "1")
        String id,
        @Schema(example = "석촌호수")
        String name,
        @Schema(example = "200.123")
        Double distance,
        @Schema(example = "2146.123")
        double length,
        @Schema(example = "트랙")
        RoadType roadType,
        @Schema(example = "CONTINUOUS_UPHILL")
        InclineSummary inclineSummary,
        @Schema(example = "쉬움")
        String difficulty,
        List<SegmentWebResponse> segments
) {
    public static List<CourseWebResponse> from(List<CourseResponse> courseResponses) {
        return courseResponses.stream()
                .map(CourseWebResponse::from).toList();
    }

    public static CourseWebResponse from(CourseResponse courseResponse) {
        return new CourseWebResponse(
                courseResponse.id(),
                courseResponse.name(),
                courseResponse.distance()
                        .map(Meter::value)
                        .orElse(null),
                courseResponse.length().value(),
                courseResponse.roadType(),
                courseResponse.inclineSummary(),
                courseResponse.difficulty().name(),
                SegmentWebResponse.from(courseResponse.segments())
        );
    }
}
