package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.InclineType;
import coursepick.coursepick.domain.Segment;

import java.util.List;

public record SegmentResponse(
        InclineType inclineType,
        List<Coordinate> coordinates
) {
    public static List<SegmentResponse> from(List<Segment> segments) {
        return segments.stream()
                .map(segment -> new SegmentResponse(segment.inclineType(), segment.coordinates()))
                .toList();
    }
}
