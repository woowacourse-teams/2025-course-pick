package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class GpxCourseParserTest {

    @Test
    void GPX_파일을_파싱하여_코스정보를_가져온다() {
        String text = """
                <?xml version="1.0" encoding="UTF-8"?>
                <gpx xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd" creator="StravaGPX" version="1.1" xmlns="http://www.topografix.com/GPX/1/1" xmlns:gpxtpx="http://www.garmin.com/xmlschemas/TrackPointExtension/v1" xmlns:gpxx="http://www.garmin.com/xmlschemas/GpxExtensions/v3">
                 <metadata>
                  <time>2025-07-20T23:36:00Z</time>
                 </metadata>
                 <trk>
                  <name>test-course</name>
                  <type>running</type>
                  <trkseg>
                   <trkpt lat="37.4869510" lon="126.9230870">
                    <ele>27.8</ele>
                    <time>2025-07-20T23:36:00Z</time>
                    <extensions>
                     <gpxtpx:TrackPointExtension>
                      <gpxtpx:cad>129</gpxtpx:cad>
                     </gpxtpx:TrackPointExtension>
                    </extensions>
                   </trkpt>
                   <trkpt lat="37.4869510" lon="126.9230870">
                    <ele>27.8</ele>
                    <time>2025-07-20T23:36:01Z</time>
                    <extensions>
                     <gpxtpx:TrackPointExtension>
                      <gpxtpx:cad>129</gpxtpx:cad>
                     </gpxtpx:TrackPointExtension>
                    </extensions>
                   </trkpt>
                   <trkpt lat="37.4845100" lon="126.9255380">
                    <ele>29.2</ele>
                    <time>2025-07-20T23:38:03Z</time>
                    <extensions>
                     <gpxtpx:TrackPointExtension>
                      <gpxtpx:cad>93</gpxtpx:cad>
                     </gpxtpx:TrackPointExtension>
                    </extensions>
                   </trkpt>
                  </trkseg>
                 </trk>
                </gpx>
                """;

        InputStream inputStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        GpxCourseParser gpxCourseParser = new GpxCourseParser();

        List<Course> courses = gpxCourseParser.parse(inputStream);

        assertThat(courses.size()).isEqualTo(1);
        assertThat(courses).extracting(course -> course.name())
                .contains("test-course");
        assertThat(courses).extracting(course -> course.coordinates().size())
                .contains(3);
        assertThat(courses).extracting(course -> course.coordinates())
                .containsExactly(List.of(
                        new Coordinate(37.4869510, 126.9230870, 27.8),
                        new Coordinate(37.4845100, 126.9255380, 29.2),
                        new Coordinate(37.4869510, 126.9230870, 27.8)
                ));
    }
}
