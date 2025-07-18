package coursepick.coursepick.domain;

import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.distance.GeodesicSphereDistCalc;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;

public record Line(
        Coordinate start,
        Coordinate end
) {
    private static final double EARTH_RADIUS_METERS = 6371000.0;

    public static Line between(Coordinate start, Coordinate end) {
        return new Line(start, end);
    }

    public Distance length() {
        ShapeFactory shapeFactory = SpatialContext.GEO.getShapeFactory();
        DistanceCalculator distCalc = new GeodesicSphereDistCalc.Haversine();
        Point point1 = shapeFactory.pointXY(start.longitude(), start.latitude());
        Point point2 = shapeFactory.pointXY(end.longitude(), end.latitude());

        double distanceInDegrees = distCalc.distance(point1, point2);
        double distanceInMeters = convertDegreeToMeter(distanceInDegrees);
        return new Distance(distanceInMeters);
    }

    public Distance distanceTo(Coordinate target) {
        double projectionRatio = target.projectionRatioBetween(start, end);
        if (projectionRatio < 0) {
            return Line.between(target, start).length();
        }
        if (projectionRatio > 1) {
            return Line.between(target, end).length();
        }
        Coordinate closestCoordinate = start.moveTo(end, projectionRatio);
        return Line.between(target, closestCoordinate).length();
    }

    private static double convertDegreeToMeter(double distanceInDegrees) {
        return distanceInDegrees * EARTH_RADIUS_METERS * Math.PI / 180.0;
    }
}
