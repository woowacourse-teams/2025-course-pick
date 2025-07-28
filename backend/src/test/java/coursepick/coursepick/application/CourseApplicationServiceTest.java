package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.application.exception.NotFoundException;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import coursepick.coursepick.infrastructure.RegionClient;
import coursepick.coursepick.test_util.DatabaseCleaner;
import coursepick.coursepick.test_util.DatabaseInserter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CourseApplicationServiceTest {

    @Autowired
    CourseApplicationService sut;

    @Autowired
    DatabaseInserter databaseInserter;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @Autowired
    CourseRepository courseRepository;

    @MockitoBean
    RegionClient regionClient;

    @AfterEach
    void tearDown() {
        databaseCleaner.deleteCourses();
    }

    @Test
    void 가까운_코스들을_조회한다() {
        Course course1 = new Course("한강 러닝 코스", "서울 송파구 잠실본동", List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ));
        Course course2 = new Course("양재천 산책길", "서울 강남구 대치동", List.of(
                new Coordinate(37.5165, 127.0285),
                new Coordinate(37.5160, 127.0278),
                new Coordinate(37.5155, 127.0265),
                new Coordinate(37.5165, 127.0285)
        ));
        Course course3 = new Course("북악산 둘레길", "서울 종로구 부암동", List.of(
                new Coordinate(37.602500, 126.967000),
                new Coordinate(37.603000, 126.968000),
                new Coordinate(37.603500, 126.969000),
                new Coordinate(37.602500, 126.967000)
        ));
        databaseInserter.saveCourse(course1);
        databaseInserter.saveCourse(course2);
        databaseInserter.saveCourse(course3);
        double latitude = 37.5172;
        double longitude = 127.0276;

        List<CourseResponse> courses = sut.findNearbyCourses(latitude, longitude);

        assertThat(courses).hasSize(2)
                .extracting(CourseResponse::name)
                .containsExactlyInAnyOrder(course1.name(), course2.name());
        assertThat(course1.distanceFrom(new Coordinate(latitude, longitude)).value()).isLessThan(1000.0);
        assertThat(course2.distanceFrom(new Coordinate(latitude, longitude)).value()).isLessThan(1000.0);
        assertThat(course3.distanceFrom(new Coordinate(latitude, longitude)).value()).isGreaterThan(1000.0);
    }

    @Test
    void 코스의_좌표_중에서_가장_가까운_좌표를_계산한다() {
        Course course = new Course("한강 러닝 코스", "서울 송파구 잠실본동", List.of(
                new Coordinate(0, 0),
                new Coordinate(10, 10),
                new Coordinate(0, 0)
        ));
        databaseInserter.saveCourse(course);
        Coordinate result = sut.findClosestCoordinate(course.id(), 5, 0);

        assertThat(result).isEqualTo(new Coordinate(2.5, 2.5));
    }

    @Test
    void 코스가_존재하지_않을_경우_예외가_발생한다() {
        assertThatThrownBy(() -> sut.findClosestCoordinate(1L, 0, 0))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void gpx파일을_파싱하고_코스를_저장한다() {
        String gpxContent = """
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
        InputStream inputStream = new ByteArrayInputStream(gpxContent.getBytes(StandardCharsets.UTF_8));
        when(regionClient.convertCoordinateToRegion(new Coordinate(37.486951, 126.923087, 27.8)))
                .thenReturn("서울시 송파구 신청동");

        sut.parseInputStreamAndSave(inputStream, "gpx");

        List<Course> courses = courseRepository.findAll();
        assertThat(courses).hasSize(1);
        assertThat(courses.get(0).name()).isEqualTo("test-course");
        assertThat(courses.get(0).region()).isEqualTo("서울시 송파구 신청동");
    }

    @Test
    void kml파일을_파싱하고_코스를_저장한다() {
        String kmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <kml xmlns="http://www.opengis.net/kml/2.2">
                <Document>
                    <Placemark>
                        <name>test-course</name>
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
        when(regionClient.convertCoordinateToRegion(new Coordinate(37.522489, 127.099029)))
                .thenReturn("서울시 송파구 신청동");

        sut.parseInputStreamAndSave(inputStream, "kml");

        List<Course> courses = courseRepository.findAll();
        assertThat(courses).hasSize(1);
        assertThat(courses.get(0).name()).isEqualTo("test-course");
        assertThat(courses.get(0).region()).isEqualTo("서울시 송파구 신청동");
    }
}
