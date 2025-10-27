package coursepick.coursepick.batch;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static coursepick.coursepick.test_util.GpxTestUtil.createGpxInputStreamOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBatchTest
class CourseBatchTest extends AbstractIntegrationTest {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    JobRepositoryTestUtils jobRepositoryTestUtils;

    @AfterEach
    void tearDown() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void 코스_싱크_배치잡이_정상적으로_작동한다() throws Exception {
        var file1 = new CourseFile("파일1", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(1, 1, 1), new Coordinate(2, 2, 2)));
        var file2 = new CourseFile("파일2", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(2, 2, 2), new Coordinate(3, 3, 3)));
        var file3 = new CourseFile("파일3", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(3, 3, 3), new Coordinate(4, 4, 4)));
        var file4 = new CourseFile("파일4", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(4, 4, 4), new Coordinate(5, 5, 5)));
        when(courseFileFetcher.fetchNextPage())
                .thenReturn(List.of(file1, file2, file3))
                .thenReturn(List.of(file4))
                .thenReturn(List.of())
                .thenThrow(IllegalStateException.class);
        var params = new JobParametersBuilder()
                .addLocalDateTime("timestamp", LocalDateTime.now())
                .toJobParameters();

        var execution = jobLauncherTestUtils.launchJob(params);

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(execution.getStepExecutions()).hasSize(1);
        var syncStep = execution.getStepExecutions().stream()
                .filter(s -> s.getStepName().equals("syncStep"))
                .findFirst()
                .orElseThrow();
        assertThat(syncStep.getReadCount()).isEqualTo(4);
        assertThat(syncStep.getFilterCount()).isEqualTo(0);
        assertThat(syncStep.getWriteCount()).isEqualTo(4);
    }

    @Test
    void 이미_동일한_해시의_코스가_존재한다면_넘어간다() throws Exception {
        String id1 = "507f1f77bcf86cd799439011";
        String id2 = "507f1f77bcf86cd799439022";
        String id3 = "507f1f77bcf86cd799439033";
        String id4 = "507f1f77bcf86cd799439044";

        var file1 = new CourseFile("파일1", CourseFileExtension.GPX, createGpxInputStreamOf(id1, new Coordinate(1, 1, 1), new Coordinate(2, 2, 2)));
        var file2 = new CourseFile("파일2", CourseFileExtension.GPX, createGpxInputStreamOf(id2, new Coordinate(2, 2, 2), new Coordinate(3, 3, 3)));
        var file3 = new CourseFile("파일3", CourseFileExtension.GPX, createGpxInputStreamOf(id3, new Coordinate(3, 3, 3), new Coordinate(4, 4, 4)));
        var file4 = new CourseFile("파일4", CourseFileExtension.GPX, createGpxInputStreamOf(id4, new Coordinate(4, 4, 4), new Coordinate(5, 5, 5)));
        when(courseFileFetcher.fetchNextPage())
                .thenReturn(List.of(file1, file2, file3))
                .thenReturn(List.of(file4))
                .thenReturn(List.of())
                .thenThrow(IllegalStateException.class);
        var params = new JobParametersBuilder()
                .addLocalDateTime("timestamp", LocalDateTime.now())
                .toJobParameters();
        dbUtil.saveCourse(new Course(id1, "파일1", List.of(new Coordinate(1, 1, 1), new Coordinate(2, 2, 2))));
        dbUtil.saveCourse(new Course(id3, "파일3", List.of(new Coordinate(3, 3, 3), new Coordinate(4, 4, 4))));

        var execution = jobLauncherTestUtils.launchJob(params);

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(execution.getStepExecutions()).hasSize(1);
        var syncStep = execution.getStepExecutions().stream()
                .filter(s -> s.getStepName().equals("syncStep"))
                .findFirst()
                .orElseThrow();
        assertThat(syncStep.getReadCount()).isEqualTo(4);
        assertThat(syncStep.getFilterCount()).isEqualTo(2);
        assertThat(syncStep.getWriteCount()).isEqualTo(2);
    }
}
