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
        Meter total = Meter.zero();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate coord1 = coordinates.get(i);
            Coordinate coord2 = coordinates.get(i + 1);

            Meter meter = GeoLine.between(coord1, coord2).length();
            total = total.add(meter);
        }

        return total.value();
    }

    public double minDistanceFrom(Coordinate target) {
        Meter min = Meter.max();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate lineStart = coordinates.get(i);
            Coordinate lineEnd = coordinates.get(i + 1);

            Meter meter = GeoLine.between(lineStart, lineEnd).distanceTo(target);
            min = min.minimum(meter);
        }

        return min.value();
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
