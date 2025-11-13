package coursepick.coursepick.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends Repository<Course, Long> {

    void saveAll(Iterable<Course> courses);

    List<Course> findAll();

    Optional<Course> findById(Long id);

    @Query(value = """
            SELECT DISTINCT c.*
            FROM course c
            INNER JOIN query_coordinates qc ON c.id = qc.course_id
            WHERE ST_Distance_Sphere(
                POINT(:longitude, :latitude),
                POINT(qc.longitude, qc.latitude)
            ) <= :distance
            """, nativeQuery = true)
    List<Course> findAllHasDistanceWithin(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distance") double distance
    );

    boolean existsByName(CourseName courseName);
}
