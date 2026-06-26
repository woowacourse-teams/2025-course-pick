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

    @Test
    void 이름이_없는_트랙은_무시하고_사유를_남긴다() {
        var gpxContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <gpx version="1.1" creator="Coursepick" xmlns="http://www.topografix.com/GPX/1/1">
                    <trk>
                        <trkseg>
                            <trkpt lat="37.48" lon="126.92"/>
                            <trkpt lat="37.49" lon="126.93"/>
                        </trkseg>
                    </trk>
                </gpx>
                """;
        var inputStream = new java.io.ByteArrayInputStream(gpxContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        var result = sut.parse(new CourseFile("테스트코스", CourseFileExtension.GPX, inputStream), ADMIN_USER);

        assertThat(result.courses()).isEmpty();
        assertThat(result.skippedReasons()).hasSize(1);
        assertThat(result.skippedReasons().get(0)).contains("이름 누락");
    }
}
