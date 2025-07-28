package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.SegmentResponse;
import coursepick.coursepick.domain.InclineType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SegmentWebResponse(
        @Schema(example = "UPHILL")
        InclineType inclineType,
        List<CoordinateWebResponse> coordinates
) {
    public static List<SegmentWebResponse> from(List<SegmentResponse> segmentResponses) {
        return segmentResponses.stream()
                .map(segmentResponse -> new SegmentWebResponse(segmentResponse.inclineType(), CoordinateWebResponse.from(segmentResponse.coordinates())))
                .toList();
    }
}
