package coursepick.coursepick.domain;

import java.util.List;

public class Course {

    private final String name;
    private final List<Coordinate> coordinates;

    public Course(String name, List<Coordinate> coordinates) {
        String compactName = compactName(name);
        validateNameLength(compactName);
        validateCoordinatesCount(coordinates);
        validateFirstLastCoordinateHasSameLatitudeAndLongitude(coordinates);
        this.name = compactName;
        this.coordinates = coordinates;
    }

    public double length() {
        double totalLength = 0;
        for (int idx = 0; idx < coordinates.size() - 1; idx++) {
            Coordinate coordinate1 = coordinates.get(idx);
            Coordinate coordinate2 = coordinates.get(idx + 1);

            totalLength += coordinate1.distanceFrom(coordinate2);
        }

        return totalLength;
    }

    public String name() {
        return name;
    }

    public double minDistanceFrom(Coordinate target) {
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate start = coordinates.get(i);
            Coordinate end = coordinates.get(i + 1);

            double distance = distanceFromPointToLineSegment(target, start, end);
            minDistance = Math.min(minDistance, distance);
        }

        return minDistance;
    }

    private double distanceFromPointToLineSegment(Coordinate target, Coordinate start, Coordinate end) {
        double distanceRatio = target.calculateDistanceRatioBetween(start, end);
        if (distanceRatio < 0) return target.distanceFrom(start);
        if (distanceRatio > 1) return target.distanceFrom(end);
        Coordinate closestCoordinate = start.moveTo(end, distanceRatio);
        return target.distanceFrom(closestCoordinate);
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
