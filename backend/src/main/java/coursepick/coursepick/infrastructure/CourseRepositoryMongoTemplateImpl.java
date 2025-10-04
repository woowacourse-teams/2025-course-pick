package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryMongoTemplateImpl implements CourseRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void saveAll(Iterable<? extends Course> courses) {
        /*
        배치 삽입을 통해 성능을 높일 수 있다. 단, 현재 새벽2시에만 호출되는 메서드라, 크게 필요해보이지는 않는다.
        또한, 현재 메모리 문제가 더 크므로 일단은 넘긴다. 필요한 경우 아래 코드를 참고할 것
        BulkOperations ops = mongoTemplate.bulkOps(BulkMode.UNORDERED, Course.class);
        ops.insert(courses);
        ops.execute();
         */
        List<? extends Course> listCourses = StreamSupport.stream(courses.spliterator(), false)
                .toList();
        mongoTemplate.insertAll(listCourses);
    }

    @Override
    public List<Course> findAllHasDistanceWithin(Coordinate target, Meter distance) {
        if (target == null || distance == null) return List.of();

        Point center = new GeoJsonPoint(target.longitude(), target.latitude());
        Query query = Query.query(Criteria.where("segments").near(center).maxDistance(distance.value()));
        return mongoTemplate.find(query, Course.class);
    }

    @Override
    public List<Course> findByIdIn(List<String> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        Query query = Query.query(Criteria.where("_id").in(ids));
        return mongoTemplate.find(query, Course.class);
    }

    @Override
    public Optional<Course> findById(String id) {
        if (id == null) return Optional.empty();

        return Optional.ofNullable(mongoTemplate.findById(id, Course.class));
    }

    @Override
    public boolean existsByName(CourseName courseName) {
        if (courseName == null) return false;

        Query query = Query.query(Criteria.where("name").is(courseName.value()));
        return mongoTemplate.exists(query, Course.class);
    }
}
