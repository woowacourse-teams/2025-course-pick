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
        SELECT * FROM course
        WHERE ST_Distance(
            line_string,
            ST_SRID(POINT(:longitude, :latitude), 4326)
        ) <= :meters
        """, nativeQuery = true)
    List<Course> findAllHasDistanceWithin(
        @Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("meters") double meters
    );

    boolean existsByName(CourseName courseName);
}
