package coursepick.coursepick.domain.course;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@AllArgsConstructor(access = AccessLevel.PUBLIC, onConstructor_ = @PersistenceCreator)
@Getter
@Accessors(fluent = true)
public class Course {

    @Id
    private final String id;

    @Indexed(name = "idx_name", unique = true)
    private CourseName name;

    private RoadType roadType;

    private InclineSummary inclineSummary;

    @GeoSpatialIndexed(name = "idx_geo_segments", type = GeoSpatialIndexType.GEO_2DSPHERE)
    private List<Segment> segments;

    private Meter length;

    private Difficulty difficulty;

    public Course(String name, RoadType roadType, List<Coordinate> rawCoordinates) {
        this(null, name, roadType, rawCoordinates);
    }

    private Course(String id, String name, RoadType roadType, List<Coordinate> rawCoordinates) {
        this.id = id;
        this.name = new CourseName(name);
        this.roadType = roadType;
        this.segments = refineCoordinates(rawCoordinates);
        this.length = calculateLength(segments);
        this.inclineSummary = InclineSummary.of(segments);
        this.difficulty = Difficulty.fromLengthAndRoadType(length(), roadType);
    }

    public static Course createFromUser(List<Coordinate> coordinates, String name, RoadType roadType, Difficulty difficulty) {
        Course course = new Course(name, roadType, coordinates);
        course.difficulty = difficulty;

        return course;
    }

    private List<Segment> refineCoordinates(List<Coordinate> rawCoordinates) {
        List<Coordinate> coordinates = CoordinateBuilder.fromRawCoordinates(rawCoordinates)
                .removeSimilar()
                .smooth()
                .build();
        List<GeoLine> geoLines = GeoLineBuilder.fromCoordinates(coordinates)
                .build();
        return SegmentBuilder.fromGeoLines(geoLines)
                .mergeSameElevationDirection()
                .mergeSameInclineType()
                .build();
    }

    private static Meter calculateLength(List<Segment> segments) {
        return segments.stream()
                .map(Segment::length)
                .reduce(Meter.zero(), Meter::add);
    }

    public Course(String id, String name, List<Coordinate> coordinates) {
        this(id, name, RoadType.알수없음, coordinates);
    }

    public Course(String name, List<Coordinate> coordinates) {
        this(null, name, RoadType.알수없음, coordinates);
    }

    public Meter distanceFrom(Coordinate target) {
        Coordinate minDistanceCoordinate = closestCoordinateFrom(target);
        return GeoLine.between(minDistanceCoordinate, target).length();
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

    public void changeCoordinates(List<Coordinate> coordinates) {
        this.segments = refineCoordinates(coordinates);
        this.length = calculateLength(segments);
        this.inclineSummary = InclineSummary.of(segments);
        this.difficulty = Difficulty.fromLengthAndRoadType(length, roadType);
    }

    public void changeName(String courseName) {
        this.name = new CourseName(courseName);
    }

    public void changeRoadType(RoadType roadType) {
        this.roadType = roadType;
        this.difficulty = Difficulty.fromLengthAndRoadType(this.length, this.roadType);
    }
}
