package coursepick.coursepick.domain.course;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import org.junit.jupiter.api.Test;

import java.util.List;

import static coursepick.coursepick.test_util.GpxTestUtil.createGpxInputStreamOf;
import static org.assertj.core.api.Assertions.assertThat;

class GpxTest {

    @Test
    void CourseFile_객체를_Gpx로_변환한다() {
        var inputStream = createGpxInputStreamOf(
                new Coordinate(0, 0, 0),
                new Coordinate(0.00001, 0.00001, 0.00001),
                new Coordinate(0, 0, 0)
        );
        var courseFile = new CourseFile("테스트코스", CourseFileExtension.GPX, inputStream);

        var sut = Gpx.from(courseFile);

        assertThat(sut.toXmlContent().strip()).containsIgnoringWhitespaces(
                """
                        <trkpt lat="0" lon="0"> <ele>0</ele> </trkpt>""",
                """
                        <trkpt lat="0.00001" lon="0.00001"> <ele>0.00001</ele> </trkpt>""",
                """
                        <trkpt lat="0" lon="0"> <ele>0</ele> </trkpt>"""
        );
    }

    @Test
    void Course_객체를_Gpx로_변환한다() {
        var coordinates = List.of(
                new Coordinate(0, 0, 0),
                new Coordinate(0.00001, 0.00001, 0.00001),
                new Coordinate(0, 0, 0)
        );
        var course = new Course("테스트코스", coordinates);

        var sut = Gpx.from(course);

        assertThat(sut.toXmlContent().strip()).containsIgnoringWhitespaces(
                """
                        <trkpt lat="0" lon="0"> <ele>0</ele> </trkpt>""",
                """
                        <trkpt lat="0.00001" lon="0.00001"> <ele>0.00001</ele> </trkpt>""",
                """
                        <trkpt lat="0" lon="0"> <ele>0</ele> </trkpt>"""
        );
    }

    @Test
    void Gpx_객체를_Xml로_변환한다() {
        var coordinates = List.of(
                new Coordinate(0, 0, 0),
                new Coordinate(0.00001, 0.00001, 0.00001),
                new Coordinate(0, 0, 0)
        );
        var course = new Course("테스트코스", coordinates);
        var sut = Gpx.from(course);

        var xml = sut.toXmlContent();

        assertThat(xml.strip()).containsIgnoringWhitespaces(
                """
                        <trkpt lat="0" lon="0"> <ele>0</ele> </trkpt>""",
                """
                        <trkpt lat="0.00001" lon="0.00001"> <ele>0.00001</ele> </trkpt>""",
                """
                        <trkpt lat="0" lon="0"> <ele>0</ele> </trkpt>"""
        );
    }

    @Test
    void Gpx_깩체를_Course로_변환한다() {
        var coordinates = List.of(
                new Coordinate(0, 0, 0),
                new Coordinate(0.00001, 0.00001, 0.00001),
                new Coordinate(0, 0, 0)
        );
        var course = new Course("테스트코스", coordinates);
        var sut = Gpx.from(course);

        var courses = sut.toCourses();

        assertThat(courses.getFirst().segments().getFirst().coordinates()).containsExactlyInAnyOrder(
                new Coordinate(0, 0, 0),
                new Coordinate(0.00001, 0.00001, 0.00001),
                new Coordinate(0, 0, 0)
        );
    }
}
