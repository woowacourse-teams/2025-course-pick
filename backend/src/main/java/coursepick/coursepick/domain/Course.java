package coursepick.coursepick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Column(nullable = false, length = 50)
    private final String name;

    @Enumerated(EnumType.STRING)
    private final RoadType roadType;

    @ElementCollection
    @CollectionTable(name = "coordinate")
    private final List<Coordinate> coordinates;

    public Course(String name, RoadType roadType, List<Coordinate> coordinates) {
        String compactName = compactName(name);
        validateNameLength(compactName);
        validateCoordinatesCount(coordinates);
        validateFirstLastCoordinateHasSameLatitudeAndLongitude(coordinates);
        this.id = null;
        this.name = compactName;
        this.roadType = roadType;
        this.coordinates = coordinates;
    }

    public Course(String name, List<Coordinate> coordinates) {
        this(name, RoadType.알수없음, coordinates);
    }

    public Meter length() {
        Meter total = Meter.zero();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate coord1 = coordinates.get(i);
            Coordinate coord2 = coordinates.get(i + 1);

            Meter meter = GeoLine.between(coord1, coord2).length();
            total = total.add(meter);
        }

        return total;
    }

    public Coordinate minDistanceCoordinate(Coordinate target) {
        Coordinate minDistanceCoordinate = coordinates.getFirst();
        Meter minDistance = Meter.max();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate lineStart = coordinates.get(i);
            Coordinate lineEnd = coordinates.get(i + 1);

            Coordinate curMinDistanceCoordinate = GeoLine.between(lineStart, lineEnd).minDistanceCoordinateTo(target);
            Meter curDistance = GeoLine.between(target, curMinDistanceCoordinate).length();
            if (curDistance.isWithin(minDistance)) {
                minDistance = curDistance;
                minDistanceCoordinate = curMinDistanceCoordinate;
            }
        }

        return minDistanceCoordinate;
    }

    public Meter minDistanceFrom(Coordinate target) {
        Coordinate minDistanceCoordinate = minDistanceCoordinate(target);
        return GeoLine.between(minDistanceCoordinate, target).length();
    }

    public double difficulty() {
        Meter length = length();
        if (length.isWithin(Meter.zero())) return 1.0;

        double score = switch (roadType) {
            case RoadType.보도, RoadType.알수없음 -> 1 + (9.0 / 42195) * length.value();
            case RoadType.트랙 -> 1.0 + (9.0 / 60000) * length.value();
            case RoadType.트레일 -> 1.0 + (9.0 / 22000) * length.value();
        };

        return Math.clamp(score, 1, 10);
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public List<Coordinate> coordinates() {
        return coordinates;
    }

    public RoadType roadType() {
        return roadType;
    }

    private static String compactName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    private static void validateNameLength(String compactName) {
        if (compactName.length() < 2 || compactName.length() > 30) {
            throw new IllegalArgumentException(INVALID_NAME_LENGTH.message(compactName));
        }
    }

    private static void validateCoordinatesCount(List<Coordinate> coordinates) {
        if (coordinates.size() < 2) {
            throw new IllegalArgumentException(INVALID_COORDINATE_COUNT.message(coordinates.size()));
        }
    }

    private static void validateFirstLastCoordinateHasSameLatitudeAndLongitude(List<Coordinate> coordinates) {
        Coordinate first = coordinates.getFirst();
        Coordinate last = coordinates.getLast();
        if (!first.hasSameLatitudeAndLongitude(last)) {
            throw new IllegalArgumentException(NOT_CONNECTED_COURSE.message(first, last));
        }
    }
}
