package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KmlCourseParserTest {

    private static final KmlCourseParser sut = new KmlCourseParser();

    @Test
    void KML_파일을_파싱하여_코스_정보를_추출한다(@TempDir Path tempDir) throws IOException {
        // given
        String kmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <kml xmlns="http://www.opengis.net/kml/2.2">
                <Document>
                    <Placemark>
                        <name>테스트 코스</name>
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

        InputStream inputStream = new ByteArrayInputStream(kmlContent.getBytes(StandardCharsets.UTF_8));

        List<Course> courses = sut.parse("테스트코스", inputStream);

        assertThat(courses).hasSize(1);

        Course course = courses.getFirst();
        assertThat(course.name()).isEqualTo("테스트코스");
        assertThat(course.coordinates()).hasSize(3);

        Coordinate firstCoordinate = course.coordinates().getFirst();
        assertThat(firstCoordinate.latitude()).isEqualTo(37.522489);
        assertThat(firstCoordinate.longitude()).isEqualTo(127.099029);
    }

    @Test
    void 좌표가_없는_Placemark는_무시한다(@TempDir Path tempDir) throws IOException {
        String kmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <kml xmlns="http://www.opengis.net/kml/2.2">
                <Document>
                    <Placemark>
                        <name>테스트 코스</name>
                    </Placemark>
                </Document>
                </kml>
                """;
        InputStream inputStream = new ByteArrayInputStream(kmlContent.getBytes(StandardCharsets.UTF_8));

        List<Course> courses = sut.parse("테스트코스", inputStream);

        assertThat(courses).isEmpty();
    }
}
