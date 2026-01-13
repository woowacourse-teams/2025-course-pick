package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.course.Meter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CourseWebResponse(
        @Schema(description = "코스 ID", example = "689c3143182cecc6353cca7b")
        String id,
        @Schema(description = "코스 이름", example = "석촌호수")
        String name,
        @Schema(description = "사용자 위치로부터의 거리 (미터)", example = "200.123")
        Double distance,
        @Schema(description = "코스 전체 길이 (미터)", example = "2146.123")
        double length,
        @Schema(description = "코스를 구성하는 좌표 목록")
        List<CoordinateWebResponse> coordinates
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
                CoordinateWebResponse.from(courseResponse.coordinates())
        );
    }
}
