package coursepick.coursepick.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@RequiredArgsConstructor
public class CustomCourseRepositoryImpl implements CustomCourseRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Course> findAllHasDistanceWithin(Coordinate target, Meter length) {
        GeoJsonPoint center = new GeoJsonPoint(target.longitude(), target.latitude());
        Criteria criteria = Criteria.where("segments")
                .near(center)
                .maxDistance(length.value());
        return mongoTemplate.find(new Query(criteria), Course.class);
    }
}
