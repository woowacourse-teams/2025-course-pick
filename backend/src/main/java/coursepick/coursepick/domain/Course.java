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

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "length"))
    private final Meter length;

    private final double difficulty;

    public Course(String name, RoadType roadType, List<Coordinate> rawCoordinates) {
        this.id = null;
        this.name = new CourseName(name);
        this.roadType = roadType;
        List<Coordinate> coordinates = CoordinateBuilder.좌표들을_세팅한다(rawCoordinates)
                .첫점과_끝점의_위치가_다르면_첫점을_뒤에_추가한다()
                .중복되는_점들을_제거한다()
                .시계_반대_방향으로_정렬한다()
                .build();
        List<GeoLine> lines = GeoLineBuilder.인접한_점을_2개씩_짝지어_선들을_만든다(coordinates)
                .build();
        this.segments = SegmentBuilder.연결된_선으로부터_생성한다(lines)
                .경사_방향성이_같은_것끼리는_합친다()
                .경사_유형이_같은_것끼리는_합친다()
                .build();
        this.length = calculateLength(segments);
        this.difficulty = calculateDifficulty(length, roadType);
    }

    public Course(String name, List<Coordinate> coordinates) {
        this(name, RoadType.알수없음, coordinates);
    }

    private static Meter calculateLength(List<Segment> segments) {
        Meter total = Meter.zero();
        for (Segment segment : segments) {
            total = total.add(segment.length());
        }
        return total;
    }

    private static double calculateDifficulty(Meter length, RoadType roadType) {
        if (length.isWithin(Meter.zero())) return 1.0;

        double score = switch (roadType) {
            case RoadType.보도, RoadType.알수없음 -> 1 + (9.0 / 42195) * length.value();
            case RoadType.트랙 -> 1.0 + (9.0 / 60000) * length.value();
            case RoadType.트레일 -> 1.0 + (9.0 / 22000) * length.value();
        };

        return Math.clamp(score, 1, 10);
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
}
