package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.GeoLine;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class CoordinateTestUtil {

    public static final double ROOT2 = sqrt(2);
    public static final double STEP_METER = 100.0;
    public static final double EARTH_RADIUS_METER = 6371000;

    public static Coordinate up(Coordinate target, double meter) {
        double deltaLat = (meter / EARTH_RADIUS_METER) * (180 / PI);

        return new Coordinate(target.latitude() + deltaLat, target.longitude());
    }

    public static Coordinate down(Coordinate target, double meter) {
        double deltaLat = (meter / EARTH_RADIUS_METER) * (180 / PI);

        return new Coordinate(target.latitude() - deltaLat, target.longitude());
    }

    public static Coordinate right(Coordinate target, double meter) {
        double deltaLng = (meter / (EARTH_RADIUS_METER * cos(toRadians(target.latitude())))) * (180 / PI);
        return new Coordinate(target.latitude(), target.longitude() + deltaLng);
    }

    public static List<Coordinate> square(Coordinate start, double toUpMeter, double toRightMeter) {
        return square(start, right(up(start, toUpMeter), toRightMeter));
    }

    public static List<Coordinate> square(Coordinate start, Coordinate end) {
        return square(start.latitude(), start.longitude(), end.latitude(), end.longitude());
    }

    public static List<Coordinate> square(double lat1, double lng1, double lat2, double lng2) {
        List<Coordinate> result = new ArrayList<>();

        Coordinate coordinate1 = new Coordinate(lat1, lng1);
        Coordinate coordinate2 = new Coordinate(lat1, lng2);
        Coordinate coordinate3 = new Coordinate(lat2, lng2);
        Coordinate coordinate4 = new Coordinate(lat2, lng1);

        result.addAll(line(coordinate1, coordinate2));
        result.removeLast();
        result.addAll(line(coordinate2, coordinate3));
        result.removeLast();
        result.addAll(line(coordinate3, coordinate4));
        result.removeLast();
        result.addAll(line(coordinate4, coordinate1));
        result.removeLast();

        return result;
    }

    public static List<Coordinate> line(Coordinate start, Coordinate end) {
        return line(start.latitude(), start.longitude(), end.latitude(), end.longitude());
    }

    public static List<Coordinate> line(double lat1, double lng1, double lat2, double lng2) {
        List<Coordinate> result = new ArrayList<>();

        double length = GeoLine.between(new Coordinate(lat1, lng1), new Coordinate(lat2, lng2)).length().value();

        int steps = (int) floor(length / STEP_METER);
        double dLat = lat2 - lat1;
        double dLng = lng2 - lng1;

        result.add(new Coordinate(lat1, lng1));
        for (int i = 1; i <= steps; i++) {
            double dist = i * STEP_METER;
            if (dist >= length) break;
            double t = dist / length;
            double lat = lat1 + t * dLat;
            double lng = lng1 + t * dLng;
            result.add(new Coordinate(lat, lng));
        }
        result.add(new Coordinate(lat2, lng2));
        return result;
    }

    public static Coordinate left(Coordinate target, double meter) {
        double deltaLng = (meter / (EARTH_RADIUS_METER * cos(toRadians(target.latitude())))) * (180 / PI);
        return new Coordinate(target.latitude(), target.longitude() - deltaLng);
    }

    public static Coordinate upright(Coordinate target, double meter) {
        return right(up(target, meter), meter);
    }

    public static Coordinate upleft(Coordinate target, double meter) {
        return left(up(target, meter), meter);
    }

    public static Coordinate downright(Coordinate target, double meter) {
        return right(down(target, meter), meter);
    }

    public static Coordinate downleft(Coordinate target, double meter) {
        return left(down(target, meter), meter);
    }
}
