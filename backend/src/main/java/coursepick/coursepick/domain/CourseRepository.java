package coursepick.coursepick.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CourseRepository extends MongoRepository<Course, String>, CustomCourseRepository {

    Optional<Course> findById(String id);

//    @Query("""
//            db.course.find({
//              'segments': {
//                $near: {
//                  $geometry: { type: 'Point', coordinates: [ ?#{#target.longitude}, ?#{#target.latitude} ] },
//                  $maxDistance: ?#{#meter.value}
//                }
//              }
//            })
//            """)
//    List<Course> findAllHasDistanceWithin(
//            @Param("target") Coordinate target,
//            @Param("meter") Meter meter
//    );

    boolean existsByName(CourseName courseName);
}
