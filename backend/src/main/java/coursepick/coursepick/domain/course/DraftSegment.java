package coursepick.coursepick.domain.course;

import java.util.ArrayList;
import java.util.List;

public record DraftSegment(
        List<Coordinate> coordinates,
        Meter length
) {
    public DraftSegment {
        coordinates = List.copyOf(coordinates);
    }

    public static DraftSegment empty() {
        return new DraftSegment(List.of(), Meter.zero());
    }

    public static DraftSegment of(List<Coordinate> coordinates) {
        return new DraftSegment(coordinates, GeoLine.totalLength(coordinates));
    }

    public DraftSegment merge(DraftSegment next) {
        if (this.coordinates.isEmpty()) {
            return new DraftSegment(next.coordinates, this.length.add(next.length));
        }
        List<Coordinate> merged = new ArrayList<>(this.coordinates);
        merged.addAll(next.coordinates.subList(1, next.coordinates.size()));
        return new DraftSegment(merged, this.length.add(next.length));
    }
}
