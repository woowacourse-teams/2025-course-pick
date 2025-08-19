package coursepick.coursepick.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@Getter
@Accessors(fluent = true)
public class Course {

    @Id
    private final String id;

    private final CourseName name;

    private final RoadType roadType;

    @GeoSpatialIndexed(name = "idx_geo_segments", type = GeoSpatialIndexType.GEO_2DSPHERE)
    private final List<Segment> segments;

    private final Meter length;

    private final Difficulty difficulty;

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
