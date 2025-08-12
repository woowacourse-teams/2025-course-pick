package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.Meter;
import coursepick.coursepick.domain.RoadType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public record CourseWebResponse(
        @Schema(example = "1")
        long id,
        @Schema(example = "석촌호수")
        String name,
        @Schema(example = "200.123")
        Double distance,
        @Schema(example = "2146.123")
        double length,
        @Schema(example = "트랙")
        RoadType roadType,
        @Schema(example = "쉬움")
        String difficulty,
        String summary,
        List<SegmentWebResponse> segments
) {
    public static List<CourseWebResponse> from(List<CourseResponse> courseResponses) {
        List<String> courseSummary = List.of(
                "초반에 오르막이 집중된 코스입니다.",
                "전반적으로 평탄한 코스입니다."
        );
        List<CourseWebResponse> responses = new ArrayList<>();
        for(int i=0;i<courseResponses.size();i++) {
            CourseResponse courseResponse = courseResponses.get(i);
            responses.add(
                    new CourseWebResponse(
                            courseResponse.id(),
                            courseResponse.name(),
                            courseResponse.distance().map(Meter::value).orElse(null),
                            courseResponse.length().value(),
                            courseResponse.roadType(),
                            courseResponse.difficulty().name(),
                            courseSummary.get(i % 2),
                            SegmentWebResponse.from(courseResponse.segments())
                    )
            );
        }
        return responses;
    }
}
