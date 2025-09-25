package coursepick.coursepick.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String> {

    default Page<Course> findAllHasDistanceWithin(Coordinate target, Meter distance, Pageable pageable) {
        return findAllHasDistanceWithin(new GeoJsonPoint(target.longitude(), target.latitude()), distance.value(), pageable);
    }

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
    Page<Course> findAllHasDistanceWithin(GeoJsonPoint target, double radius, Pageable pageable);

    List<Course> findByIdIn(List<String> ids);

    boolean existsByName(CourseName name);
}
