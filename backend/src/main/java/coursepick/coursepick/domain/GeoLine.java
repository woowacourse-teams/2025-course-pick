package coursepick.coursepick.domain;

import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.distance.GeodesicSphereDistCalc;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;

public record GeoLine(
        Coordinate start,
        Coordinate end
) {
    private static final double EARTH_RADIUS_METERS = 6371000.0;

    public static GeoLine between(Coordinate start, Coordinate end) {
        return new GeoLine(start, end);
    }

    public Meter length() {
        ShapeFactory shapeFactory = SpatialContext.GEO.getShapeFactory();
        DistanceCalculator distCalc = new GeodesicSphereDistCalc.Haversine();
        Point point1 = shapeFactory.pointXY(start.longitude(), start.latitude());
        Point point2 = shapeFactory.pointXY(end.longitude(), end.latitude());

        double distanceInDegrees = distCalc.distance(point1, point2);
        double distanceInMeters = convertDegreeToMeter(distanceInDegrees);
        return new Meter(distanceInMeters);
    }

    public Meter distanceTo(Coordinate target) {
        double projectionRatio = target.projectionRatioBetween(start, end);
        if (projectionRatio < 0) {
            return GeoLine.between(target, start).length();
        }
        if (projectionRatio > 1) {
            return GeoLine.between(target, end).length();
        }
        Coordinate closestCoordinate = start.moveTo(end, projectionRatio);
        return GeoLine.between(target, closestCoordinate).length();
    }

    private static double convertDegreeToMeter(double distanceInDegrees) {
        return distanceInDegrees * EARTH_RADIUS_METERS * Math.PI / 180.0;
    }
}
