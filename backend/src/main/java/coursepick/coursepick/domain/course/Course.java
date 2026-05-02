package coursepick.coursepick.domain.course;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.user.User;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document
@AllArgsConstructor(access = AccessLevel.PUBLIC, onConstructor_ = @PersistenceCreator)
@Getter
@Accessors(fluent = true)
public class Course {

    private static final int REPORT_ALERT_THRESHOLD = 3;

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

    private Set<String> reportUserIds;

    public Course(String id, CourseName courseName, List<Coordinate> rawCoordinates, User user) {
        this.id = id;
        this.name = courseName;
        this.coordinates = refineCoordinates(rawCoordinates);
        this.simplifiedCoordinates = simplifyCoordinates(this.coordinates);
        this.length = calculateLength(coordinates);
        this.reviews = new ArrayList<>();
        this.creatorId = user.id();
        this.reportUserIds = new HashSet<>();
    }

    private List<Coordinate> refineCoordinates(List<Coordinate> rawCoordinates) {
        return CoordinateBuilder.fromRawCoordinates(rawCoordinates)
                .removeSimilar()
                .smooth()
                .build();
    }

    private List<Coordinate> simplifyCoordinates(List<Coordinate> coordinates) {
        return CoordinateBuilder.fromRawCoordinates(coordinates)
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
        this.coordinates = refineCoordinates(coordinates);
        this.simplifiedCoordinates = simplifyCoordinates(this.coordinates);
        this.length = calculateLength(this.coordinates);
    }

    public void changeName(String courseName) {
        this.name = new CourseName(courseName);
    }

    public void addReview(User author, String content) {
        reviews.add(new Review(author, content));
    }

    public void addReport(User user) {
        if (reportUserIds.contains(user.id())) {
            throw ErrorType.ALREADY_REPORTED_COURSE.create(this.id, user.id());
        }
        reportUserIds.add(user.id());
    }

    public boolean isReportThreshold() {
        return reportUserIds.size() >= REPORT_ALERT_THRESHOLD;
    }
}
