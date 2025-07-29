package coursepick.coursepick.domain;

import lombok.experimental.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Helper
public class SegmentBuilder {

    private final List<Segment> segments;

    private SegmentBuilder(List<Segment> segments) {
        this.segments = segments;
    }

    public static SegmentBuilder 연결된_선으로부터_생성한다(List<GeoLine> geoLines) {
        List<Segment> segments = geoLines.stream()
                .map(GeoLine::toSegment)
                .toList();

        return new SegmentBuilder(segments);
    }

    // 경향성이 같은 것끼리 합친다.
    public SegmentBuilder 경사_방향성이_같은_것끼리는_합친다() {
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
        return new SegmentBuilder(mergedSegments);
    }

    // 경사타입이 같은 것끼리 합친다.
    public SegmentBuilder 경사_유형이_같은_것끼리는_합친다() {
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
        return new SegmentBuilder(mergedSegments);
    }

    public List<Segment> build() {
        return Collections.unmodifiableList(segments);
    }
}
