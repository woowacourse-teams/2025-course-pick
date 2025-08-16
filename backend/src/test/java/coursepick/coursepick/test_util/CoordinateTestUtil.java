package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.GeoLine;

import java.util.ArrayList;
import java.util.List;

public class CoordinateTestUtil {

    private static final double STEP_METER = 100.0;

    public static List<Coordinate> square(Coordinate start, Coordinate end) {
        return square(start.latitude(), start.longitude(), end.latitude(), end.longitude());
    }

    public static List<Coordinate> square(double lat1, double lng1, double lat2, double lng2) {
        List<Coordinate> result = new ArrayList<>();

        Coordinate coordinate1 = new Coordinate(lat1, lng1);
        Coordinate coordinate2 = new Coordinate(lat1, lng2);
        Coordinate coordinate3 = new Coordinate(lat2, lng2);
        Coordinate coordinate4 = new Coordinate(lat2, lng1);

        result.add(coordinate1);
        result.addAll(line(coordinate1, coordinate2));
        result.add(coordinate2);
        result.addAll(line(coordinate2, coordinate3));
        result.add(coordinate3);
        result.addAll(line(coordinate3, coordinate4));
        result.add(coordinate4);
        result.addAll(line(coordinate4, coordinate1));

        return result;
    }

    public static List<Coordinate> line(Coordinate start, Coordinate end) {
        return line(start.latitude(), start.longitude(), end.latitude(), end.longitude());
    }

    public static List<Coordinate> line(double lat1, double lng1, double lat2, double lng2) {
        List<Coordinate> result = new ArrayList<>();

        double length = GeoLine.between(new Coordinate(lat1, lng1), new Coordinate(lat2, lng2)).length().value();

        int steps = (int) Math.floor(length / STEP_METER);
        double dLat = lat2 - lat1;
        double dLng = lng2 - lng1;

        for (int i = 1; i <= steps; i++) {
            double dist = i * STEP_METER;
            if (dist >= length) break;
            double t = dist / length;
            double lat = lat1 + t * dLat;
            double lng = lng1 + t * dLng;
            result.add(new Coordinate(lat, lng));
        }
        return result;
    }
}
