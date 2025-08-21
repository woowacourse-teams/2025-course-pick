package coursepick.coursepick.batch;

import coursepick.coursepick.application.CourseParserService;
import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.infrastructure.GpxCourseParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static coursepick.coursepick.test_util.GpxTestUtil.createGpxInputStreamOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourseReaderTest {

    CourseFileFetcher courseFileFetcher;
    CourseReader sut;

    @BeforeEach
    void setUp() {
        courseFileFetcher = mock(CourseFileFetcher.class);
        sut = new CourseReader(courseFileFetcher, new CourseParserService(List.of(new GpxCourseParser())));
    }

    @Test
    void 코스를_하나씩_읽어들인다() throws Exception {
        var file1 = new CourseFile("파일1", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(1, 1, 1), new Coordinate(2, 2, 2)));
        var file2 = new CourseFile("파일2", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(2, 2, 2), new Coordinate(3, 3, 3)));
        var file3 = new CourseFile("파일3", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(3, 3, 3), new Coordinate(4, 4, 4)));
        var file4 = new CourseFile("파일4", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(4, 4, 4), new Coordinate(5, 5, 5)));
        when(courseFileFetcher.fetchNextPage())
                .thenReturn(List.of(file1, file2))
                .thenReturn(List.of(file3, file4))
                .thenReturn(List.of())
                .thenThrow(IllegalStateException.class);

        var result1 = sut.read();
        var result2 = sut.read();
        var result3 = sut.read();
        var result4 = sut.read();
        var result5 = sut.read();

        assertThat(result1.name().value()).isEqualTo("파일1");
        assertThat(result2.name().value()).isEqualTo("파일2");
        assertThat(result3.name().value()).isEqualTo("파일3");
        assertThat(result4.name().value()).isEqualTo("파일4");
        assertThat(result5).isNull();
    }

    @Test
    void 각_페이지의_파일_개수가_달라도_정상적으로_읽는다() throws Exception {
        var file1 = new CourseFile("파일1", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(1, 1, 1), new Coordinate(2, 2, 2)));
        var file2 = new CourseFile("파일2", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(2, 2, 2), new Coordinate(3, 3, 3)));
        var file3 = new CourseFile("파일3", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(3, 3, 3), new Coordinate(4, 4, 4)));
        var file4 = new CourseFile("파일4", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(4, 4, 4), new Coordinate(5, 5, 5)));
        when(courseFileFetcher.fetchNextPage())
                .thenReturn(List.of(file1, file2, file3))
                .thenReturn(List.of(file4))
                .thenReturn(List.of())
                .thenThrow(IllegalStateException.class);

        var result1 = sut.read();
        var result2 = sut.read();
        var result3 = sut.read();
        var result4 = sut.read();
        var result5 = sut.read();

        assertThat(result1.name().value()).isEqualTo("파일1");
        assertThat(result2.name().value()).isEqualTo("파일2");
        assertThat(result3.name().value()).isEqualTo("파일3");
        assertThat(result4.name().value()).isEqualTo("파일4");
        assertThat(result5).isNull();
    }

    @Test
    void 코스_파일을_읽을_때에는_끝까지_읽는다() throws IOException {
        var file1 = new CourseFile("파일1", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(1, 1, 1), new Coordinate(2, 2, 2)));
        var file2 = new CourseFile("파일2", CourseFileExtension.GPX, createGpxInputStreamOf(new Coordinate(2, 2, 2), new Coordinate(3, 3, 3)));
        when(courseFileFetcher.fetchNextPage())
                .thenReturn(List.of(file1, file2))
                .thenReturn(List.of())
                .thenThrow(IllegalStateException.class);

        sut.read();
        sut.read();
        sut.read();

        assertThat(file1.inputStream().available()).isEqualTo(0);
        assertThat(file2.inputStream().available()).isEqualTo(0);
    }
}
