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

    @GeoSpatialIndexed(name = "idx_geo_coordinates", type = GeoSpatialIndexType.GEO_2DSPHERE)
    private List<Coordinate> coordinates;

    private Meter length;

    public Course(String id, String name, List<Coordinate> rawCoordinates) {
        this.id = id;
        this.name = new CourseName(name);
        this.coordinates = refineCoordinates(rawCoordinates);
        this.length = calculateLength(coordinates);
    }

    private List<Coordinate> refineCoordinates(List<Coordinate> rawCoordinates) {
        return CoordinateBuilder.fromRawCoordinates(rawCoordinates)
                .removeSimilar()
                .smooth()
                .build();
    }

    private static Meter calculateLength(List<Coordinate> coordinates) {
        Meter totalLength = Meter.zero();
        for (int i = 0; i < coordinates.size() - 1; i++) {
            Meter length = GeoLine.between(coordinates.get(i), coordinates.get(i + 1)).length();
            totalLength = totalLength.add(length);
        }
        return totalLength;
    }

    public Meter distanceFrom(Coordinate target) {
        Coordinate minDistanceCoordinate = closestCoordinateFrom(target);
        return GeoLine.between(minDistanceCoordinate, target).length();
    }

    public Coordinate closestCoordinateFrom(Coordinate target) {
        Coordinate closestCoordinate = coordinates.getFirst();
        Meter minDistance = Meter.max();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            GeoLine line = GeoLine.between(coordinates.get(i), coordinates.get(i + 1));
            Coordinate closestCoordinateOnLine = line.closestCoordinateFrom(target);
            Meter distanceOnLine = GeoLine.between(target, closestCoordinateOnLine).length();
            if (distanceOnLine.isWithin(minDistance)) {
                minDistance = distanceOnLine;
                closestCoordinate = closestCoordinateOnLine;
            }
        }

        return closestCoordinate;
    }

    public void changeCoordinates(List<Coordinate> coordinates) {
        this.coordinates = refineCoordinates(coordinates);
        this.length = calculateLength(this.coordinates);
    }

    public void changeName(String courseName) {
        this.name = new CourseName(courseName);
    }
}
