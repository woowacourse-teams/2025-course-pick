package coursepick.coursepick.domain;

import coursepick.coursepick.test_util.DatabaseInserter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(DatabaseInserter.class)
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DatabaseInserter databaseInserter;

    @ParameterizedTest
    @CsvSource({
            "800, 3",
            "700, 2",
            "600, 1"
    })
    void 거리를_줄여가면서_검색되는_코스_수가_줄어든다(int distance, int expectedSize) {
        Course course1 = new CircleCourse("잠실 한강 산책길", List.of(
                new Coordinate(37.522490, 127.099029),
                new Coordinate(37.521818, 127.096380),
                new Coordinate(37.520995, 127.095899),
                new Coordinate(37.519581, 127.092719),
                new Coordinate(37.519263, 127.091775),
                new Coordinate(37.519230, 127.091406),
                new Coordinate(37.519151, 127.091336),
                new Coordinate(37.518645, 127.091510),
                new Coordinate(37.519916, 127.094579),
                new Coordinate(37.520656, 127.096037),
                new Coordinate(37.520775, 127.095937),
                new Coordinate(37.521230, 127.097186),
                new Coordinate(37.521544, 127.097899),
                new Coordinate(37.522273, 127.099238),
                new Coordinate(37.522490, 127.099029)
        ));
        Course course2 = new CircleCourse("잠실 종합운동장", List.of(
                new Coordinate(37.517802, 127.069576),
                new Coordinate(37.510638, 127.070661),
                new Coordinate(37.511926, 127.078170),
                new Coordinate(37.517109, 127.077165),
                new Coordinate(37.518201, 127.072168),
                new Coordinate(37.517802, 127.069576)
        ));
        Course course3 = new CircleCourse("잠실제일교회 둘레길", List.of(
                new Coordinate(37.517396, 127.092439),
                new Coordinate(37.512785, 127.094059),
                new Coordinate(37.513460, 127.097520),
                new Coordinate(37.514285, 127.097306),
                new Coordinate(37.516743, 127.094943),
                new Coordinate(37.517897, 127.094506),
                new Coordinate(37.517396, 127.092439)
        ));
        databaseInserter.saveCourse(course1);
        databaseInserter.saveCourse(course2);
        databaseInserter.saveCourse(course3);
        Coordinate target = new Coordinate(37.514647, 127.086592);

        List<Course> courses = courseRepository.findAllHasDistanceWithin(target, new Meter(distance));

        assertThat(courses).hasSize(expectedSize);
    }
}
