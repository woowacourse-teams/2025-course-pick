package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.InclineType;
import coursepick.coursepick.domain.Segment;

import java.util.List;

public record AdminSegmentWebResponse(
        InclineType inclineType,
        List<AdminCoordinateWebResponse> coordinates
) {
    public static List<AdminSegmentWebResponse> from(List<Segment> segments) {
        return segments.stream()
                .map(segmentResponse -> new AdminSegmentWebResponse(segmentResponse.inclineType(), AdminCoordinateWebResponse.from(segmentResponse.coordinates())))
                .toList();
    }
}
