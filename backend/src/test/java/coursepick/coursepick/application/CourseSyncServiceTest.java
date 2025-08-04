package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.test_util.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;

class CourseSyncServiceTest extends IntegrationTest {

    @Autowired
    CourseSyncService sut;

    @Test
    void 코스의_싱크를_맞춘다() {
        InputStream gpxInputStream1 = gpxUtil.createGpxInputStreamOf(new Coordinate(1, 1, 1), new Coordinate(2, 2, 2), new Coordinate(3, 3, 3));
        InputStream gpxInputStream2 = gpxUtil.createGpxInputStreamOf(new Coordinate(1, 1, 1), new Coordinate(2, 2, 2), new Coordinate(3, 3, 3), new Coordinate(1, 1, 1));
        when(courseFileFetcher.fetchAll()).thenReturn(List.of(
                new CourseFile("코스1", CourseFileExtension.GPX, gpxInputStream1),
                new CourseFile("코스2", CourseFileExtension.GPX, gpxInputStream2)
        ));

        sut.runCourseSyncJob();

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> assertThat(dbUtil.countCourses()).isEqualTo(2));
    }
}
