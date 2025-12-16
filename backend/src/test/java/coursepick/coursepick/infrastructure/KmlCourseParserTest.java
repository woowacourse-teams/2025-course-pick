package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.infrastructure.course_parser.KmlCourseParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

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

        var courses = sut.parse(new CourseFile("테스트코스", CourseFileExtension.KML, inputStream));

        assertThat(courses).hasSize(1);
        var course = courses.getFirst();
        assertThat(course.name().value()).isEqualTo("테스트코스");
        var firstCoordinate = course.segments().getFirst().startCoordinate();
        assertThat(firstCoordinate.latitude()).isEqualTo(37.5224898);
        assertThat(firstCoordinate.longitude()).isEqualTo(127.0990294);
    }

    @Test
    void 좌표가_없는_Placemark는_무시한다() {
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

        var courses = sut.parse(new CourseFile("테스트코스", CourseFileExtension.KML, inputStream));

        assertThat(courses).isEmpty();
    }
}
