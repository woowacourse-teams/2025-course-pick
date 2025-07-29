package coursepick.coursepick.domain;

import lombok.experimental.Helper;

import java.util.ArrayList;
import java.util.List;

@Helper
public class SegmentHelper {

    private final List<Segment> segments;

    private SegmentHelper(List<Segment> segments) {
        this.segments = segments;
    }

    public static SegmentHelper from(List<Coordinate> coordinates) {
        List<Segment> segments = new CoordinateHelper(coordinates)
                .connectStartEnd()
                .sortByCounterClockwise()
                .toGeoLines()
                .stream().map(GeoLine::toSegment)
                .toList();

        return new SegmentHelper(segments);
    }

    // 경향성이 같은 것끼리 합친다.
    public SegmentHelper mergeSameDirection() {
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
        return new SegmentHelper(mergedSegments);
    }

    // 경사타입이 같은 것끼리 합친다.
    public SegmentHelper mergeSameInclineType() {
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
        return new SegmentHelper(mergedSegments);
    }

    public List<Segment> segments() {
        return segments;
    }
}
