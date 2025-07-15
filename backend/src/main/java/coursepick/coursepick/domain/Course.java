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
