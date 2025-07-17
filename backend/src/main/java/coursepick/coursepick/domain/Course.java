package coursepick.coursepick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.distance.GeodesicSphereDistCalc;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Column(nullable = false, length = 50)
    private final String name;

    @ElementCollection
    @CollectionTable(name = "coordinate")
    private final List<Coordinate> coordinates;

    public Course(String name, List<Coordinate> coordinates) {
        String compactName = compactName(name);
        validateNameLength(compactName);
        validateCoordinatesCount(coordinates);
        validateFirstLastCoordinateHasSameLatitudeAndLongitude(coordinates);
        this.id = null;
        this.name = compactName;
        this.coordinates = coordinates;
    }

    // Spatial4J 기반 코스 길이 계산
    public double length() {
        ShapeFactory shapeFactory = SpatialContext.GEO.getShapeFactory();
        DistanceCalculator distCalc = new GeodesicSphereDistCalc.Haversine();

        double totalLength = 0;
        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate coord1 = coordinates.get(i);
            Coordinate coord2 = coordinates.get(i + 1);

            Point point1 = createPoint(coord1, shapeFactory);
            Point point2 = createPoint(coord2, shapeFactory);

            double distanceInDegrees = distCalc.distance(point1, point2);
            double distanceInMeters = convertDegreeToMeter(distanceInDegrees);
            totalLength += distanceInMeters;
        }

        return totalLength;
    }

    // Spatial4J 기반 최단 거리 계산
    public double minDistanceFrom(Coordinate target) {
        ShapeFactory shapeFactory = SpatialContext.GEO.getShapeFactory();
        DistanceCalculator distCalc = new GeodesicSphereDistCalc.Haversine();

        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate start = coordinates.get(i);
            Coordinate end = coordinates.get(i + 1);

            double distance = distanceFromPointToLine(target, start, end, shapeFactory, distCalc);
            minDistance = Math.min(minDistance, distance);
        }

        return minDistance;
    }

    public String name() {
        return name;
    }

    public List<Coordinate> coordinates() {
        return coordinates;
    }

    // Spatial4J 기반 점-선분 거리 계산
    private static double distanceFromPointToLine(
            Coordinate target,
            Coordinate start,
            Coordinate end,
            ShapeFactory shapeFactory,
            DistanceCalculator distCalc
    ) {
        Point targetPoint = createPoint(target, shapeFactory);
        Point startPoint = createPoint(start, shapeFactory);
        Point endPoint = createPoint(end, shapeFactory);

        double projectionRatio = target.calculateProjectionRatioBetween(start, end);

        if (projectionRatio < 0) {
            double distanceInDegrees = distCalc.distance(targetPoint, startPoint);
            return convertDegreeToMeter(distanceInDegrees);
        }
        if (projectionRatio > 1) {
            double distanceInDegrees = distCalc.distance(targetPoint, endPoint);
            return convertDegreeToMeter(distanceInDegrees);
        }

        // 투영점 계산
        Coordinate closestCoordinate = start.moveTo(end, projectionRatio);
        Point closestPoint = createPoint(closestCoordinate, shapeFactory);

        double distanceInDegrees = distCalc.distance(targetPoint, closestPoint);
        return convertDegreeToMeter(distanceInDegrees);
    }

    private static Point createPoint(Coordinate target, ShapeFactory shapeFactory) {
        return shapeFactory.pointXY(target.longitude(), target.latitude());
    }

    private static double convertDegreeToMeter(double distanceInDegrees) {
        final double earthRadiusMeters = 6371000.0;
        return distanceInDegrees * earthRadiusMeters * Math.PI / 180.0;
    }

    private static String compactName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    private static void validateNameLength(String compactName) {
        if (compactName.length() < 2 || compactName.length() > 30) {
            throw new IllegalArgumentException();
        }
    }

    private static void validateCoordinatesCount(List<Coordinate> coordinates) {
        if (coordinates.size() < 2) {
            throw new IllegalArgumentException();
        }
    }

    private static void validateFirstLastCoordinateHasSameLatitudeAndLongitude(List<Coordinate> coordinates) {
        if (!coordinates.getFirst().hasSameLatitudeAndLongitude(coordinates.getLast())) {
            throw new IllegalArgumentException();
        }
    }
}
