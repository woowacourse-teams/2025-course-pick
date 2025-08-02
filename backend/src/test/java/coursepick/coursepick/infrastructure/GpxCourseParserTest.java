package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.domain.Course;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GpxCourseParserTest {

    @Test
    void GPX_파일을_파싱하여_코스정보를_가져온다() {
        String text = """
                <?xml version="1.0" encoding="UTF-8"?>
                <gpx xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd" creator="StravaGPX" version="1.1" xmlns="http://www.topografix.com/GPX/1/1" xmlns:gpxtpx="http://www.garmin.com/xmlschemas/TrackPointExtension/v1" xmlns:gpxx="http://www.garmin.com/xmlschemas/GpxExtensions/v3">
                 <trk>
                  <name>test-course</name>
                  <type>running</type>
                  <trkseg>
                   <trkpt lat="37.4869510" lon="126.9230870">
                    <ele>27.8</ele>
                   </trkpt>
                   <trkpt lat="37.4869515" lon="126.9230875">
                    <ele>27.8</ele>
                   </trkpt>
                   <trkpt lat="37.4845100" lon="126.9255380">
                    <ele>29.2</ele>
                   </trkpt>
                  </trkseg>
                 </trk>
                </gpx>
                """;

        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        GpxCourseParser gpxCourseParser = new GpxCourseParser();

        List<Course> courses = gpxCourseParser.parse(new CourseFile("테스트코스", CourseFileExtension.KML, inputStream));

        assertThat(courses.size()).isEqualTo(1);
        assertThat(courses).extracting(course -> course.name().value())
                .contains("테스트코스");
    }
}
