package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.infrastructure.parser.GpxCourseParser;
import coursepick.coursepick.test_util.GpxTestUtil;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GpxCourseParserTest {

    GpxCourseParser sut = new GpxCourseParser();
    GpxTestUtil gpxUtil = new GpxTestUtil();

    @Test
    void GPX_파일을_파싱하여_코스정보를_가져온다() {
        InputStream inputStream = gpxUtil.createGpxInputStreamOf(
                new Coordinate(37.4869510, 126.9230870, 27.8),
                new Coordinate(37.4869515, 126.9230875, 27.8),
                new Coordinate(37.4845100, 126.9255380, 29.2)
        );

        List<Course> courses = sut.parse(new CourseFile("테스트코스", CourseFileExtension.GPX, inputStream));

        assertThat(courses.size()).isEqualTo(1);
        assertThat(courses).extracting(course -> course.name().value())
                .contains("테스트코스");
    }
}
