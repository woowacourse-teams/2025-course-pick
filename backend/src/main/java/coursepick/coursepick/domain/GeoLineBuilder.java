package coursepick.coursepick.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeoLineBuilder {

    private final List<GeoLine> lines;

    private GeoLineBuilder(List<GeoLine> lines) {
        this.lines = lines;
    }

    public static GeoLineBuilder fromCoordinates(List<Coordinate> coordinates) {
        List<GeoLine> lines = new ArrayList<>();
        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate front = coordinates.get(i);
            Coordinate back = coordinates.get(i + 1);
            lines.add(GeoLine.between(front, back));
        }
        return new GeoLineBuilder(lines);
    }

    public List<GeoLine> build() {
        return Collections.unmodifiableList(lines);
    }
}
