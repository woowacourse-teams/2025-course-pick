package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.batch.CourseFileFetcher;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.test_util.DatabaseTestUtil;
import coursepick.coursepick.test_util.GpxTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Import({GpxTestUtil.class, DatabaseTestUtil.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CourseSyncServiceTest {

    @Autowired
    CourseSyncService sut;

    @MockitoBean
    CourseFileFetcher courseFileFetcher;

    @Autowired
    GpxTestUtil gpxUtil;

    @Autowired
    DatabaseTestUtil dbUtil;

    @AfterEach
    void tearDown() {
        dbUtil.deleteCourses();
    }

    @Test
    void 코스의_싱크를_맞춘다() throws InterruptedException {
        InputStream gpxInputStream1 = gpxUtil.createGpxInputStreamOf(new Coordinate(1, 1, 1), new Coordinate(2, 2, 2), new Coordinate(3, 3, 3));
        InputStream gpxInputStream2 = gpxUtil.createGpxInputStreamOf(new Coordinate(1, 1, 1), new Coordinate(2, 2, 2), new Coordinate(3, 3, 3), new Coordinate(1, 1, 1));
        when(courseFileFetcher.fetchAll()).thenReturn(List.of(
                new CourseFile("코스1", CourseFileExtension.GPX, gpxInputStream1),
                new CourseFile("코스2", CourseFileExtension.GPX, gpxInputStream2)
        ));

        sut.runCourseSyncJob();

        Thread.sleep(1000);
        assertThat(dbUtil.countCourses()).isEqualTo(2);
    }
}
