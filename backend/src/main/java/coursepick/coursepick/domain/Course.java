package coursepick.coursepick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@Accessors(fluent = true)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Embedded
    private final CourseName name;

    @Enumerated(EnumType.STRING)
    private final RoadType roadType;

    @ElementCollection
    @CollectionTable(name = "segment")
    private final List<Segment> segments;

    public Course(String name, RoadType roadType, List<Coordinate> rowCoordinates) {
        this.id = null;
        this.name = new CourseName(name);
        this.roadType = roadType;
        List<Coordinate> coordinates = CoordinateHelper.fromRowCoordinates(rowCoordinates)
                .connectStartEnd()
                .sortByCounterClockwise()
                .build();
        List<GeoLine> lines = GeoLineHelper.fromCoordinates(coordinates)
                .build();
        this.segments = SegmentHelper.fromLines(lines)
                .mergeSameDirection()
                .mergeSameInclineType()
                .build();
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
}
