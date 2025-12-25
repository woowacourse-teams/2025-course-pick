package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.SegmentResponse;
import coursepick.coursepick.domain.course.InclineType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SegmentWebResponse(
        @Schema(description = "경사 타입 (UPHILL: 오르막, DOWNHILL: 내리막, FLAT: 평지)", example = "UPHILL")
        InclineType inclineType,
        @Schema(description = "세그먼트를 구성하는 좌표 목록")
        List<CoordinateWebResponse> coordinates
) {
    public static List<SegmentWebResponse> from(List<SegmentResponse> segmentResponses) {
        return segmentResponses.stream()
                .map(segmentResponse -> new SegmentWebResponse(segmentResponse.inclineType(), CoordinateWebResponse.from(segmentResponse.coordinates())))
                .toList();
    }
}
