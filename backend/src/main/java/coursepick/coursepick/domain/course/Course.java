package coursepick.coursepick.domain.course;

import coursepick.coursepick.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jspecify.annotations.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
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

    private List<Coordinate> coordinates;

    @GeoSpatialIndexed(name = "idx_geo_simplified_coordinates", type = GeoSpatialIndexType.GEO_2DSPHERE)
    private List<Coordinate> simplifiedCoordinates;

    private Meter length;

    private List<Review> reviews;

    private String creatorId;

    public Course(String id, CourseName courseName, List<Coordinate> coordinates, User user) {
        this.id = id;
        this.name = courseName;
        this.coordinates = coordinates;
        this.simplifiedCoordinates = simplifyCoordinates(this.coordinates);
        this.length = calculateLength(this.coordinates);
        this.reviews = new ArrayList<>();
        this.creatorId = user.id();
    }

    public Course(String id, List<RawCoordinate> rawCoordinates, CourseName courseName, User user) {
        this.id = id;
        this.name = courseName;
        this.coordinates = convertCourseCoordinate(rawCoordinates);
        this.simplifiedCoordinates = optimizeCoordinates(rawCoordinates);
        this.length = calculateLength(coordinates);
        this.reviews = new ArrayList<>();
        this.creatorId = user.id();
    }

    private List<Coordinate> convertCourseCoordinate(List<RawCoordinate> rawCoordinates) {
        return rawCoordinates.stream()
                .map(raw -> new Coordinate(raw.latitude(), raw.longitude()))
                .toList();
    }

    private List<Coordinate> simplifyCoordinates(List<Coordinate> coordinates) {
        return CoordinateBuilder.fromCoordinates(coordinates)
                .simplify(new Meter(10))
                .build();
    }

    private List<Coordinate> optimizeCoordinates(List<RawCoordinate> rawCoordinates) {
        return CoordinateBuilder.fromRawCoordinates(rawCoordinates)
                .removeSimilar(new Meter(100))
                .smooth()
                .simplify(new Meter(10))
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
        this.coordinates = coordinates;
        this.simplifiedCoordinates = simplifyCoordinates(coordinates);
        this.length = calculateLength(this.coordinates);
    }

    public void changeName(String courseName) {
        this.name = new CourseName(courseName);
    }

    public void addReview(User author, String content) {
        reviews.add(new Review(author, content));
    }
}
