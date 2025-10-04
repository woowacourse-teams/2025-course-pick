package coursepick.coursepick.domain;

import java.util.ArrayList;
import java.util.List;

public class GeoLineFactory {

    public static List<GeoLine> create(List<Coordinate> rawCoordinates) {
        List<Coordinate> coordinates = CoordinateFactory.create(rawCoordinates);

        List<GeoLine> lines = new ArrayList<>();
        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate front = coordinates.get(i);
            Coordinate back = coordinates.get(i + 1);
            lines.add(GeoLine.between(front, back));
        }

        return lines;
    }
}
