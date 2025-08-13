package coursepick.coursepick.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends MongoRepository<Course, String> {

    Optional<Course> findById(String id);

    @Query("""
            db.course.find ({
                segments: {
                    $geoWithin: {
                        $centerSphere: [
                            [
                                ?#{#target.longitude()},
                                ?#{#target.latitude()}
                            ],
                            ?#{#meter.value() / 6378100}
                        ]
                    }
                }
            })
            """)
    List<Course> findAllHasDistanceWithin(
            @Param("target") Coordinate target,
            @Param("meter") Meter meter
    );


    boolean existsByName(CourseName courseName);
}
