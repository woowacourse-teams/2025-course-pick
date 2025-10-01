package coursepick.coursepick.domain;

import java.util.ArrayList;
import java.util.List;

public class SegmentFactory {

    public static List<Segment> create(List<Coordinate> rawCoordinates) {
        List<Segment> rawSegments = GeoLineFactory.create(rawCoordinates).stream()
                .map(GeoLine::toSegment)
                .toList();

        List<Segment> mergedSegments = mergeSameElevationDirection(rawSegments);

        return mergeSameInclineType(mergedSegments);
    }

    // 경향성이 같은 것끼리 합친다.
    private static List<Segment> mergeSameElevationDirection(List<Segment> segments) {
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
        return mergedSegments;
    }

    // 경사타입이 같은 것끼리 합친다.
    private static List<Segment> mergeSameInclineType(List<Segment> segments) {
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
        return mergedSegments;
    }
}
