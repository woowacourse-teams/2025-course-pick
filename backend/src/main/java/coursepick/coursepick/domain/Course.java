package coursepick.coursepick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_COORDINATE_COUNT;
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

    @BatchSize(size = 30)
    @ElementCollection
    @CollectionTable(name = "segment")
    private final List<Segment> segments;

    public Course(String name, RoadType roadType, List<Coordinate> coordinates) {
        String compactName = compactName(name);
        validateNameLength(compactName);
        validateCoordinatesCount(coordinates);
        List<Coordinate> sortedCoordinates = sortByCounterClockwise(connectStartEndCoordinate(coordinates));
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
            throw INVALID_NAME_LENGTH.create(compactName);
        }
    }

    private static void validateCoordinatesCount(List<Coordinate> coordinates) {
        if (coordinates.size() < 2) {
            throw INVALID_COORDINATE_COUNT.create(coordinates.size());
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
        return !coordinates.getFirst().equals(coordinates.getLast());
    }

    private static List<Coordinate> sortByCounterClockwise(List<Coordinate> coordinates) {
        List<Coordinate> result = new ArrayList<>(coordinates);
        if (isClockwise(coordinates)) {
            Collections.reverse(result);
        }
        return result;
    }

    private static boolean isClockwise(List<Coordinate> coordinates) {
        int lowestCoordinateIndex = findLowestCoordinateIndex(coordinates);
        int nextIndex = (lowestCoordinateIndex + 1) % (coordinates.size() - 1);
        return coordinates.get(lowestCoordinateIndex).isRightOf(coordinates.get(nextIndex));
    }

    private static int findLowestCoordinateIndex(List<Coordinate> coordinates) {
        int lowestCoordinateIndex = 0;
        double lowestLatitude = Double.MAX_VALUE;
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate coordinate = coordinates.get(i);
            if (coordinate.latitude() < lowestLatitude) {
                lowestLatitude = coordinate.latitude();
                lowestCoordinateIndex = i;
            }
        }
        return lowestCoordinateIndex;
    }
}
