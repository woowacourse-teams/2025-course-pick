package coursepick.coursepick.infrastructure.repository;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.Meter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@RequiredArgsConstructor
public class CourseCustomRepositoryImpl implements CourseCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Course> findAllHasDistanceWithin(Coordinate target, Meter length) {
        Point center = new Point(target.longitude(), target.latitude());
        double radiusInRadians = length.value() / 6378100.0;

        Circle circle = new Circle(center, radiusInRadians);

        Criteria criteria = Criteria.where("segments").withinSphere(circle);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Course.class);
    }
}
