package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import coursepick.coursepick.infrastructure.parser.GpxCourseParser;
import org.junit.jupiter.api.Test;

import static coursepick.coursepick.test_util.GpxTestUtil.createGpxInputStreamOf;
import static coursepick.coursepick.test_util.UserFixture.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;

class GpxCourseParserTest {

    GpxCourseParser sut = new GpxCourseParser();

    @Test
    void GPX_파일을_파싱하여_코스정보를_가져온다() {
        var inputStream = createGpxInputStreamOf(
                new Coordinate(37.4869510, 126.9230870),
                new Coordinate(37.4869515, 126.9230875),
                new Coordinate(37.4845100, 126.9255380));

        var result = sut.parse(new CourseFile("테스트코스", CourseFileExtension.GPX, inputStream), ADMIN_USER);

        assertThat(result.courses().size()).isEqualTo(1);
        assertThat(result.courses().get(0).name().value()).isEqualTo("test-course");
    }
}
