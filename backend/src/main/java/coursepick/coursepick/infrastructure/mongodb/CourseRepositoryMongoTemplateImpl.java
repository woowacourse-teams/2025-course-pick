package coursepick.coursepick.infrastructure.mongodb;

import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoTimeoutException;
import coursepick.coursepick.domain.course.*;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static coursepick.coursepick.application.exception.ErrorType.QUERY_TIMEOUT;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryMongoTemplateImpl implements CourseRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void save(Course course) {
        mongoTemplate.save(course);
    }

    @Override
    public Slice<Course> findAllHasDistanceWithin(CourseFindCondition condition) {

        try {
            Query query = new Query().maxTimeMsec(5000);

            addPositionAndScopeCriteria(condition, query);
            if (condition.minLength() != null || condition.maxLength() != null) addLengthCriteria(condition, query);

            query.with(condition.pageable())
                    .limit(condition.pageSize() + 1);

            List<Course> result = mongoTemplate.find(query, Course.class);

            boolean hasNext = result.size() > condition.pageSize();
            if (hasNext) result.removeLast();
            return new SliceImpl<>(result, condition.pageable(), hasNext);
        } catch (MongoTimeoutException | MongoExecutionTimeoutException e) {
            throw QUERY_TIMEOUT.create();
        }
    }

    @Override
    public List<Course> findAllCustomCourses(String creatorId) {
        try {

            Query query = new Query().maxTimeMsec(5000);

            query.addCriteria(Criteria.where("creatorId").is(creatorId));
            query.with(Sort.by(Sort.Direction.DESC, "createdAt"));

            return mongoTemplate.find(query, Course.class);

        } catch (MongoTimeoutException | MongoExecutionTimeoutException e) {
            throw QUERY_TIMEOUT.create();
        }
    }


    private static void addPositionAndScopeCriteria(CourseFindCondition condition, Query query) {
        GeoJsonPoint point = new GeoJsonPoint(condition.mapPosition().longitude(), condition.mapPosition().latitude());

        Criteria criteria = Criteria.where("simplifiedCoordinates")
                .nearSphere(point)
                .maxDistance(condition.scope().value());

        query.addCriteria(criteria);
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

    @Override
    public boolean existByCourseName(CourseName courseName) {
        return mongoTemplate.exists(Query.query(Criteria.where("name").is(courseName.value())), Course.class);
    }

    @Override
    public void pushReview(String courseId, Review review) {
        Query query = new Query(Criteria.where("_id").is(courseId));

        Document reviewDoc = new Document()
                .append("id", new ObjectId(review.id()))
                .append("userId", review.userId())
                .append("authorNickname", review.authorNickname())
                .append("content", review.content())
                .append("rating", review.rating())
                .append("reportUserIds", review.reportUserIds())
                .append("createdAt", review.createdAt());

        Update update = new Update().push("reviews", reviewDoc);

        mongoTemplate.updateFirst(
                query,
                update,
                mongoTemplate.getCollectionName(Course.class)
        );
    }

    @Override
    public void deleteReview(String courseId, String reviewId) {

        Query query = new Query(Criteria.where("_id").is(courseId));

        Update update = new Update().pull("reviews", new Document("id", new ObjectId(reviewId)));

        mongoTemplate.updateFirst(
                query,
                update,
                mongoTemplate.getCollectionName(Course.class)
        );
    }
}
