package coursepick.coursepick.domain;

import java.util.List;

public enum InclineSummary {

    FLAT,
    REPEATING_HILLS,
    SOMETIMES_UPHILL,
    SOMETIMES_DOWNHILL,
    CONTINUOUS_UPHILL,
    CONTINUOUS_DOWNHILL,
    UNKNOWN,
    ;

    public static InclineSummary of(List<Segment> segments) {
        Meter courseLength = segments.stream()
                .map(Segment::length)
                .reduce(Meter.zero(), Meter::add);
        Meter uphillLength = sumLengthByInclineType(segments, InclineType.UPHILL);
        Meter downhillLength = sumLengthByInclineType(segments, InclineType.DOWNHILL);

        double uphillRate = courseLength.getRateOf(uphillLength);
        double downhillRate = courseLength.getRateOf(downhillLength);
        if (uphillRate >= 0.3 && downhillRate >= 0.3) {
            return REPEATING_HILLS;
        }
        if (uphillRate >= 0.5) {
            return CONTINUOUS_UPHILL;
        }
        if (downhillRate >= 0.5) {
            return CONTINUOUS_DOWNHILL;
        }
        if (uphillRate >= 0.2) {
            return SOMETIMES_UPHILL;
        }
        if (downhillRate >= 0.2) {
            return SOMETIMES_DOWNHILL;
        }
        return FLAT;
    }

    private static Meter sumLengthByInclineType(List<Segment> segments, InclineType type) {
        return segments.stream()
                .filter(segment -> segment.inclineType() == type)
                .map(Segment::length)
                .reduce(Meter.zero(), Meter::add);
    }
}
