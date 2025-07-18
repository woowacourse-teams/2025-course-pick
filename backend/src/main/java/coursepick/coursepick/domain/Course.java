package coursepick.coursepick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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

    public double length() {
        Distance totalDistance = Distance.zero();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate coord1 = coordinates.get(i);
            Coordinate coord2 = coordinates.get(i + 1);

            Distance distance = Line.between(coord1, coord2).length();
            totalDistance = totalDistance.add(distance);
        }

        return totalDistance.meter();
    }

    public double minDistanceFrom(Coordinate target) {
        Distance minDistance = Distance.max();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate lineStart = coordinates.get(i);
            Coordinate lineEnd = coordinates.get(i + 1);

            Distance distance = Line.between(lineStart, lineEnd).distanceTo(target);
            minDistance = minDistance.minimum(distance);
        }

        return minDistance.meter();
    }

    public String name() {
        return name;
    }

    public List<Coordinate> coordinates() {
        return coordinates;
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
