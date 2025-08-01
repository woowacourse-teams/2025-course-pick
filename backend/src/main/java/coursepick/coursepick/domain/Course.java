package coursepick.coursepick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.BatchSize;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static coursepick.coursepick.application.exception.ErrorType.*;

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
    @CollectionTable(name = "coordinate")
    private final List<Coordinate> coordinates;

    public Course(String name, RoadType roadType, List<Coordinate> coordinates) {
        String compactName = compactName(name);
        validateNameLength(compactName);
        validateCoordinatesCount(coordinates);
        validateCoordinateOnlyStartEndDuplicate(coordinates);

        this.id = null;
        this.name = compactName;
        this.roadType = roadType;
        this.coordinates = distinctCoordinates(coordinates);
    }

    public Course(String name, List<Coordinate> coordinates) {
        this(name, RoadType.알수없음, coordinates);
    }

    public Coordinate closestCoordinateFrom(Coordinate target) {
        Coordinate closestCoordinate = coordinates.getFirst();
        Meter minDistance = Meter.max();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            GeoLine line = GeoLine.between(coordinates.get(i), coordinates().get(i + 1));

            Coordinate closestCoordinateOnLine = line.closestCoordinateFrom(target);
            Meter distanceOnLine = GeoLine.between(target, closestCoordinateOnLine).length();
            if (distanceOnLine.isWithin(minDistance)) {
                minDistance = distanceOnLine;
                closestCoordinate = closestCoordinateOnLine;
            }
        }

        return closestCoordinate;
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

    public Meter distanceFrom(Coordinate target) {
        Coordinate minDistanceCoordinate = closestCoordinateFrom(target);
        return GeoLine.between(minDistanceCoordinate, target).length();
    }

    public Difficulty difficulty() {
        return Difficulty.fromLengthAndRoadType(length(), roadType);
    }

    public List<Segment> segments() {
        List<GeoLine> geoLines = GeoLine.split(coordinates);
        List<Segment> segments = geoLines.stream()
                .map(GeoLine::toSegment)
                .toList();

        List<Segment> sameDirectionSegments = Segment.mergeSameDirection(segments);
        return Segment.mergeSameInclineType(sameDirectionSegments);
    }

    private static List<Coordinate> distinctCoordinates(List<Coordinate> coordinates) {
        List<Coordinate> distinctCoordinates = coordinates.subList(1, coordinates.size() - 1).stream()
                .distinct()
                .collect(Collectors.toList());
        distinctCoordinates.addFirst(coordinates.getFirst());
        distinctCoordinates.add(coordinates.getLast());

        return Collections.unmodifiableList(distinctCoordinates);
    }

    private static String compactName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    private static void validateCoordinateOnlyStartEndDuplicate(List<Coordinate> coordinates) {
        if (coordinates.size() == 2 && coordinates.getFirst().equals(coordinates.getLast())) {
            throw INVALID_DUPLICATE_COORDINATE_ONLY_START_END.create();
        }
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
}
