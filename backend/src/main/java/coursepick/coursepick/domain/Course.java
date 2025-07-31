package coursepick.coursepick.domain;

import jakarta.persistence.*;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.BatchSize;
import lombok.experimental.Accessors;

import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_COORDINATE_COUNT;
import static coursepick.coursepick.application.exception.ErrorType.INVALID_NAME_LENGTH;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@Accessors(fluent = true)
public abstract class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected final Long id;

    @Column(nullable = false, length = 50)
    protected final String name;

    @Enumerated(EnumType.STRING)
    protected final RoadType roadType;

    @BatchSize(size = 30)
    @ElementCollection
    @CollectionTable(name = "coordinate")
    protected final List<Coordinate> coordinates;

    public abstract Coordinate closestCoordinateFrom(Coordinate target);

    protected Course(String name, RoadType roadType, List<Coordinate> coordinates) {
        String compactName = compactName(name);
        validateNameLength(compactName);
        validateCoordinatesCount(coordinates);

        this.id = null;
        this.name = compactName;
        this.roadType = roadType;
        this.coordinates = new ArrayList<>(coordinates);
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
}
