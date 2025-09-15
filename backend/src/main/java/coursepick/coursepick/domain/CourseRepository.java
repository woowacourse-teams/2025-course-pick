package coursepick.coursepick.domain;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String> {

    @Query("""
            {
              'segments': {
                $near: {
                  $geometry: ?0,
                  $maxDistance: ?1
                }
              }
            }
            """)
    List<Course> findAllHasDistanceWithin(Point target, double radius);

    default List<Course> findAllHasDistanceWithin(Coordinate target, Meter distance) {
        return findAllHasDistanceWithin(new Point(new Position(target.longitude(), target.latitude())), distance.value());
    }

    boolean existsByName(CourseName courseName);

    Page<Course> findByNameContains(String name, Pageable page);
}
