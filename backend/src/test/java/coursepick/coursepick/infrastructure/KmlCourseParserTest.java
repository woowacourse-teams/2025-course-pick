package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import coursepick.coursepick.infrastructure.parser.KmlCourseParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static coursepick.coursepick.test_util.UserFixture.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;

class KmlCourseParserTest {

    KmlCourseParser sut = new KmlCourseParser();

    @Test
    void KML_파일을_파싱하여_코스_정보를_추출한다() {
        var kmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <kml xmlns="http://www.opengis.net/kml/2.2">
                <Document>
                    <Placemark>
                        <name>테스트코스</name>
                        <Polygon>
                            <outerBoundaryIs>
                                <LinearRing>
                                    <coordinates>
                                        127.0990294928381,37.52248985670181,0 127.0963796423111,37.52181839278625,0 127.0990294928381,37.52248985670181,0
                                    </coordinates>
                                </LinearRing>
                            </outerBoundaryIs>
                        </Polygon>
                    </Placemark>
                </Document>
                </kml>
                """;
        var inputStream = new ByteArrayInputStream(kmlContent.getBytes(StandardCharsets.UTF_8));

        var result = sut.parse(new CourseFile("테스트코스", CourseFileExtension.KML, inputStream), ADMIN_USER);

        assertThat(result.courses()).hasSize(1);
        var course = result.courses().get(0);
        assertThat(course.name().value()).isEqualTo("테스트코스");
        var firstCoordinate = course.coordinates().getFirst();
        assertThat(firstCoordinate.latitude()).isEqualTo(37.5224898);
        assertThat(firstCoordinate.longitude()).isEqualTo(127.0990294);
    }

    @Test
    void 좌표가_없는_Placemark는_무시하고_사유를_남긴다() {
        var kmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <kml xmlns="http://www.opengis.net/kml/2.2">
                <Document>
                    <Placemark>
                        <name>테스트 코스</name>
                    </Placemark>
                </Document>
                </kml>
                """;
        var inputStream = new ByteArrayInputStream(kmlContent.getBytes(StandardCharsets.UTF_8));

        var result = sut.parse(new CourseFile("테스트코스", CourseFileExtension.KML, inputStream), ADMIN_USER);

        assertThat(result.courses()).isEmpty();
        assertThat(result.skippedReasons()).hasSize(1);
        assertThat(result.skippedReasons().get(0)).contains("좌표 부족");
    }
}
