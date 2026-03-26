package coursepick.coursepick.infrastructure.mongodb;

import com.mongodb.ExplainVerbosity;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoTimeoutException;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseFindCondition;
import coursepick.coursepick.domain.course.CourseName;
import coursepick.coursepick.domain.course.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static coursepick.coursepick.application.exception.ErrorType.QUERY_TIMEOUT;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CourseRepositoryMongoTemplateImpl implements CourseRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void save(Course course) {
        mongoTemplate.save(course);
    }

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
    public Slice<Course> findAllHasDistanceWithin(CourseFindCondition condition) {
        long start = System.nanoTime();
        log.error("쿼리 시작 시간 : {}", start);
        try {
            Query query = new Query().maxTimeMsec(5000);

            addPositionAndScopeCriteria(condition, query);
            if (condition.minLength() != null || condition.maxLength() != null) addLengthCriteria(condition, query);

            query.with(condition.pageable())
                    .limit(condition.pageSize() + 1); //다음 데이터가 있는지 확인하기 위해 + 1을 한다.

            List<Course> result = mongoTemplate.find(query, Course.class);

            boolean hasNext = result.size() > condition.pageSize();
            if (hasNext) result.removeLast();
            long end = System.nanoTime();
            log.error("끝 시간 : {}", end);
            log.error("소요 시간 : {}ms", (end - start) / 1000000);
            return new SliceImpl<>(result, condition.pageable(), hasNext);
        } catch (MongoTimeoutException | MongoExecutionTimeoutException e) {
            throw QUERY_TIMEOUT.create();
        }
    }

    private static void addPositionAndScopeCriteria(CourseFindCondition condition, Query query) {
        Point point = new Point(condition.mapPosition().longitude(), condition.mapPosition().latitude());
        Distance distance = new Distance(condition.scope().value() / 1000.0, Metrics.KILOMETERS);
        Circle circle = new Circle(point, distance);

        Criteria v2Criteria = Criteria.where("coordinates").withinSphere(circle);

        query.addCriteria(v2Criteria);

        //near
//        GeoJsonPoint point = new GeoJsonPoint(condition.mapPosition().longitude(), condition.mapPosition().latitude());
//
//        Criteria criteria = Criteria.where("coordinates")
//                .nearSphere(point)
//                .maxDistance(condition.scope().value());

        //within
//        double radiusInMeters = condition.scope().value();
//
//        // nearSphere 대신 withinSphere 사용
//        Criteria criteria = Criteria.where("coordinates")
//                .withinSphere(new Circle(point, radiusInMeters / 6378137.0));

//        query.addCriteria(criteria);
    }

    private static void addLengthCriteria(CourseFindCondition condition, Query query) {
        Criteria lengthCriteria = Criteria.where("length");
        if (condition.minLength() != null) {
            lengthCriteria.gte(condition.minLength().value());
        }
        if (condition.maxLength() != null) {
            lengthCriteria.lte(condition.maxLength().value());
        }

        query.addCriteria(lengthCriteria);
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
    public Optional<Course> findByName(CourseName courseName) {
        if (courseName == null) return Optional.empty();

        Query query = Query.query(Criteria.where("name").is(courseName.value()));

        return Optional.ofNullable(mongoTemplate.findOne(query, Course.class));
    }

    @Override
    public void delete(Course course) {
        mongoTemplate.remove(course);
    }
}
