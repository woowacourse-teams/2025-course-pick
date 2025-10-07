package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static coursepick.coursepick.test_util.GpxTestUtil.createGpxInputStreamOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;

class CourseSyncServiceTest extends AbstractIntegrationTest {

    @Autowired
    CourseSyncService sut;

    @Test
    void 코스의_싱크를_맞춘다() throws IOException {
        var gpxInputStream1 = createGpxInputStreamOf(new Coordinate(1, 1, 1), new Coordinate(2, 2, 2), new Coordinate(3, 3, 3));
        var gpxInputStream2 = createGpxInputStreamOf(new Coordinate(1, 1, 1), new Coordinate(2, 2, 2), new Coordinate(3, 3, 3), new Coordinate(1, 1, 1));
        when(courseFileFetcher.fetchNextPage())
                .thenReturn(List.of(
                        new CourseFile("코스1", CourseFileExtension.GPX, gpxInputStream1),
                        new CourseFile("코스2", CourseFileExtension.GPX, gpxInputStream2)
                ))
                .thenReturn(List.of());

        sut.runCourseSyncJob();

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> assertThat(dbUtil.countCourses()).isEqualTo(2));
    }
}
