package coursepick.coursepick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_COORDINATE_COUNT;
import static coursepick.coursepick.application.exception.ErrorType.INVALID_NAME_LENGTH;

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

        this.id = null;
        this.name = compactName;
        this.roadType = roadType;
        this.coordinates = connectStartEndCoordinate(coordinates);
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

    public Meter minDistanceFrom(Coordinate target) {
        Meter min = Meter.max();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate lineStart = coordinates.get(i);
            Coordinate lineEnd = coordinates.get(i + 1);

            Meter meter = GeoLine.between(lineStart, lineEnd).distanceTo(target);
            min = min.minimum(meter);
        }

        return min;
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

    private List<Coordinate> connectStartEndCoordinate(List<Coordinate> coordinates) {
        if (isFirstAndLastCoordinateDifferent(coordinates)) {
            coordinates = new ArrayList<>(coordinates);
            coordinates.add(coordinates.getFirst());
        }
        return coordinates;
    }

    private static boolean isFirstAndLastCoordinateDifferent(List<Coordinate> coordinates) {
        return !coordinates.getFirst().hasSameLatitudeAndLongitude(coordinates.getLast());
    }
}
