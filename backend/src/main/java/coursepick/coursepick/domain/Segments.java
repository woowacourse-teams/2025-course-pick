package coursepick.coursepick.domain;

import jakarta.persistence.Embeddable;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public record Segments(
        List<Segment> segments
) {
    public static Segments merge(List<Coordinate> coordinates) {
        return Segments.create(coordinates)
                .mergeSameDirection()
                .mergeSameInclineType();
    }

    private static Segments create(List<Coordinate> coordinates) {
        List<Segment> segments = new CoordinateHelper(coordinates)
                .connectStartEnd()
                .sortByCounterClockwise()
                .toGeoLines()
                .stream().map(GeoLine::toSegment)
                .toList();
        
        return new Segments(segments);
    }

    // 경향성이 같은 것끼리 합친다.
    private Segments mergeSameDirection() {
        List<Segment> mergedSegments = new ArrayList<>();
        mergedSegments.add(segments.getFirst());
        for (int i = 1; i < segments.size(); i++) {
            Segment beforeSegment = mergedSegments.removeLast();
            Segment currentSegment = segments.get(i);

            if (beforeSegment.isSameDirection(currentSegment)) {
                mergedSegments.add(beforeSegment.merge(currentSegment));
            } else {
                mergedSegments.add(beforeSegment);
                mergedSegments.add(currentSegment);
            }
        }
        return new Segments(mergedSegments);
    }

    // 경사타입이 같은 것끼리 합친다.
    private Segments mergeSameInclineType() {
        List<Segment> mergedSegments = new ArrayList<>();
        mergedSegments.add(segments.getFirst());
        for (int i = 1; i < segments.size(); i++) {
            Segment beforeSegment = mergedSegments.removeLast();
            Segment currentSegment = segments.get(i);
            InclineType beforeInclineType = beforeSegment.inclineType();
            InclineType currentInclineType = currentSegment.inclineType();

            if (beforeInclineType == currentInclineType) {
                mergedSegments.add(beforeSegment.merge(currentSegment));
            } else {
                mergedSegments.add(beforeSegment);
                mergedSegments.add(currentSegment);
            }
        }
        return new Segments(mergedSegments);
    }
}
