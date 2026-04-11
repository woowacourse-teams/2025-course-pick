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
        Meter length = Meter.zero();
        for (int i = 0; i < coordinates.size() - 1; i++) {
            length = length.add(GeoLine.between(coordinates.get(i), coordinates.get(i + 1)).length());
        }
        return new DraftSegment(coordinates, length);
    }

    public DraftSegment merge(DraftSegment next) {
        List<Coordinate> merged = new ArrayList<>(this.coordinates);
        merged.addAll(next.coordinates.subList(1, next.coordinates.size()));
        return new DraftSegment(merged, this.length.add(next.length));
    }
}
