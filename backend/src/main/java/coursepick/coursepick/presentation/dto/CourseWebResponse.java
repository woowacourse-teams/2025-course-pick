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
        @Schema(description = "코스를 구성하는 좌표 목록 [위도, 경도]")
        List<double[]> coordinates,
        @Schema(
                description = "코스 등록 주체\n- 운영자: null 또는 admin\n- 유저: 생성한 유저의 id",
                example = "689c1233232cecc6353cda7b"
        )
        String creator

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
                courseResponse.coordinates().stream()
                        .map(c -> new double[]{c.latitude(), c.longitude()})
                        .toList(),
                courseResponse.creator().id()
        );
    }
}
