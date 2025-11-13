package coursepick.coursepick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.BatchSize;

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

    @BatchSize(size = 30)
    @ElementCollection
    @CollectionTable(name = "segment")
    private final List<Segment> segments;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "length"))
    private final Meter length;

    @Enumerated(EnumType.STRING)
    private final Difficulty difficulty;

    /*
    1 2
    2 3
    3 4
    ---
    4 5
    5 6
    6 7


    1 2 3 4 5 6 7
    <- 세그먼트 구성 하려면 다시 높낮이 고려해야함 but 저장공간 + 역직렬화 성능 확보 가능
     */
    public Course(String name, RoadType roadType, List<Coordinate> rawCoordinates) {
        this.id = null;
        this.name = new CourseName(name);
        this.roadType = roadType;
        List<Coordinate> coordinates = CoordinateBuilder.fromRawCoordinates(rawCoordinates)
                .removeSimilar()
                .smooth()
                .build();
        List<GeoLine> geoLines = GeoLineBuilder.fromCoordinates(coordinates)
                .build();
        this.segments = SegmentBuilder.fromGeoLines(geoLines)
                .mergeSameElevationDirection()
                .mergeSameInclineType()
                .build();
        this.length = calculateLength(segments);
        this.difficulty = Difficulty.fromLengthAndRoadType(length(), roadType);
    }

    public Course(String name, List<Coordinate> coordinates) {
        this(name, RoadType.알수없음, coordinates);
    }

    public Coordinate closestCoordinateFrom(Coordinate target) {
        Coordinate minDistanceCoordinate = segments().getFirst().startCoordinate();
        Meter minDistance = Meter.max();

        for (Segment segment : segments()) {
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

    private static Meter calculateLength(List<Segment> segments) {
        Meter total = Meter.zero();
        for (Segment segment : segments) {
            total = total.add(segment.length());
        }
        return total;
    }
}
