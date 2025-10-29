package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
    public Slice<Course> findAllHasDistanceWithin(Coordinate target, Meter distance, Pageable pageable) {
        if (target == null || distance == null) return Page.empty(pageable);

        Criteria criteria = Criteria.where("segments")
                .near(new GeoJsonPoint(target.longitude(), target.latitude()))
                .maxDistance(distance.value());

        if (pageable == null) {
            Query query = Query.query(criteria);

            return new SliceImpl<>(mongoTemplate.find(query, Course.class));
        }

        Query query = Query.query(criteria)
                .with(pageable)
                .limit(pageable.getPageSize() + 1);

        List<Course> result = mongoTemplate.find(query, Course.class);

        boolean hasNext = result.size() > pageable.getPageSize();
        if (hasNext) result.removeLast();
        return new SliceImpl<>(result, pageable, hasNext);
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

    @Override
    public void delete(Course course) {
        mongoTemplate.remove(course);
    }
}
