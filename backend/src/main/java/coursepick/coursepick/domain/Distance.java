package coursepick.coursepick.domain;

import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.distance.GeodesicSphereDistCalc;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;

public record Distance(
        double meter
) {
    private static final double EARTH_RADIUS_METERS = 6371000.0;

    public static Distance zero() {
        return new Distance(0);
    }

    public static Distance max() {
        return new Distance(Double.MAX_VALUE);
    }

    public static Distance between(Coordinate coord1, Coordinate coord2) {
        ShapeFactory shapeFactory = SpatialContext.GEO.getShapeFactory();
        DistanceCalculator distCalc = new GeodesicSphereDistCalc.Haversine();
        Point point1 = shapeFactory.pointXY(coord1.longitude(), coord1.latitude());
        Point point2 = shapeFactory.pointXY(coord2.longitude(), coord2.latitude());

        double distanceInDegrees = distCalc.distance(point1, point2);
        double distanceInMeters = convertDegreeToMeter(distanceInDegrees);
        return new Distance(distanceInMeters);
    }

    public static Distance betweenPointAndLine(Coordinate target, Coordinate lineStart, Coordinate lineEnd) {
        double projectionRatio = target.calculateProjectionRatioBetween(lineStart, lineEnd);
        if (projectionRatio < 0) {
            return Distance.between(target, lineStart);
        }
        if (projectionRatio > 1) {
            return Distance.between(target, lineEnd);
        }
        Coordinate closestCoordinate = lineStart.moveTo(lineEnd, projectionRatio);
        return Distance.between(target, closestCoordinate);
    }

    public Distance add(Distance other) {
        return new Distance(this.meter() + other.meter());
    }

    public Distance minimum(Distance other) {
        return this.meter() <= other.meter() ? this : other;
    }

    private static double convertDegreeToMeter(double distanceInDegrees) {
        return distanceInDegrees * EARTH_RADIUS_METERS * Math.PI / 180.0;
    }
}
