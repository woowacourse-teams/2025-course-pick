package coursepick.coursepick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_NAME_LENGTH;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@Accessors(fluent = true)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Column(nullable = false, length = 50)
    private final String name;

    @Enumerated(EnumType.STRING)
    private final RoadType roadType;

    @ElementCollection
    @CollectionTable(name = "segment")
    private final List<Segment> segments;

    public Course(String name, RoadType roadType, List<Coordinate> coordinates) {
        String compactName = compactName(name);
        validateNameLength(compactName);
        Coordinates sortedCoordinates = new Coordinates(coordinates)
                .connectStartEnd()
                .sortByCounterClockwise();
        List<Segment> segments = Segment.create(sortedCoordinates);
        this.id = null;
        this.name = compactName;
        this.roadType = roadType;
        this.segments = segments;
    }

    public Course(String name, List<Coordinate> coordinates) {
        this(name, RoadType.알수없음, coordinates);
    }

    public Meter length() {
        Meter total = Meter.zero();
        for (Segment segment : segments) {
            total = total.add(segment.length());
        }
        return total;
    }

    public Coordinate closestCoordinateFrom(Coordinate target) {
        Coordinate minDistanceCoordinate = segments.getFirst().startCoordinate();
        Meter minDistance = Meter.max();

        for (Segment segment : segments) {
            Coordinate currentCoordinate = segment.closestCoordinateFrom(target);
            Meter currentDistance = GeoLine.between(target, currentCoordinate).length();

            if (currentDistance.isWithin(minDistance)) {
                minDistance = currentDistance;
                minDistanceCoordinate = currentCoordinate;
            }
        }

        return minDistanceCoordinate;
    }

    public Meter distanceFrom(Coordinate target) {
        Coordinate minDistanceCoordinate = closestCoordinateFrom(target);
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

    private static String compactName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    private static void validateNameLength(String compactName) {
        if (compactName.length() < 2 || compactName.length() > 30) {
            throw new IllegalArgumentException(INVALID_NAME_LENGTH.message(compactName));
        }
    }
}
