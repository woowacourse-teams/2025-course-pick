package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.InclineType;
import coursepick.coursepick.domain.course.Segment;

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
