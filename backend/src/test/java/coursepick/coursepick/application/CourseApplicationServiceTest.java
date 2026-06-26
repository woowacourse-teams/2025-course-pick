package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.application.exception.UnauthorizedException;
import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import coursepick.coursepick.test_util.CoordinateTestUtil;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static coursepick.coursepick.test_util.CourseFixture.*;
import static coursepick.coursepick.test_util.UserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CourseApplicationServiceTest extends AbstractIntegrationTest {

    @Autowired
    CourseApplicationService sut;

    @Nested
    class 코스_탐색 {

        @Test
        void 코스는_최소_1KM부터_탐색할_수_있다() {
            var course1 = createHanRiverCourse();
            var course2 = createYangjaeCourse();
            var course3 = createBukakCourse();
            dbUtil.saveCourse(course1);
            dbUtil.saveCourse(course2);
            dbUtil.saveCourse(course3);

            var latitude = 37.5122;
            var longitude = 127.0276;
            var condition = new CourseFindCondition(latitude, longitude, 300, null, null, null);

            var nearbyCourses = sut.findNearbyCourses(condition, null, null);

            assertThat(nearbyCourses.courses()).hasSize(2)
                    .extracting(CourseResponse::name)
                    .containsExactlyInAnyOrder(course1.name().value(), course2.name().value());
            assertThat(course1.distanceFrom(new Coordinate(latitude, longitude)).value()).isGreaterThan(300);
            assertThat(course2.distanceFrom(new Coordinate(latitude, longitude)).value()).isGreaterThan(300);
            assertThat(course3.distanceFrom(new Coordinate(latitude, longitude)).value()).isGreaterThan(1000.0);
        }

        @Test
        void 코스는_최대_3KM까지_탐색할_수_있다() {
            var course1 = createHanRiverCourse();
            var course2 = createCourse("먼 코스", List.of(
                    new Coordinate(38.602500, 126.967000),
                    new Coordinate(38.603000, 126.968000),
                    new Coordinate(38.603500, 126.969000),
                    new Coordinate(38.602500, 126.967000)
            ));
            dbUtil.saveCourse(course1);
            dbUtil.saveCourse(course2);

            var latitude = 37.5122;
            var longitude = 127.0276;
            var condition = new CourseFindCondition(latitude, longitude, 15000, null, null, null);

            var nearbyCourses = sut.findNearbyCourses(condition, null, null);

            assertThat(nearbyCourses.courses()).hasSize(1)
                    .extracting(CourseResponse::name)
                    .containsExactlyInAnyOrder(course1.name().value());
            assertThat(course1.distanceFrom(new Coordinate(latitude, longitude)).value()).isLessThan(3000);
            assertThat(course2.distanceFrom(new Coordinate(latitude, longitude)).value()).isGreaterThan(3000);
        }

        @Test
        void 가까운_코스들을_조회한다() {
            var course1 = createHanRiverCourse();
            var course2 = createYangjaeCourse();
            var course3 = createBukakCourse();
            dbUtil.saveCourse(course1);
            dbUtil.saveCourse(course2);
            dbUtil.saveCourse(course3);

            var latitude = 37.5172;
            var longitude = 127.0276;
            var condition = new CourseFindCondition(latitude, longitude, 1000, null, null, null);

            var courses = sut.findNearbyCourses(condition, null, null);

            assertThat(courses.courses()).hasSize(2)
                    .extracting(CourseResponse::name)
                    .containsExactlyInAnyOrder(course1.name().value(), course2.name().value());
            assertThat(course1.distanceFrom(new Coordinate(latitude, longitude)).value()).isLessThan(1000.0);
            assertThat(course2.distanceFrom(new Coordinate(latitude, longitude)).value()).isLessThan(1000.0);
            assertThat(course3.distanceFrom(new Coordinate(latitude, longitude)).value()).isGreaterThan(1000.0);
        }

        @Test
        void 가까운_코스들을_조회하고_현위치에서_거리를_계산한다() {
            var course1 = createHanRiverCourse();
            var course2 = createYangjaeCourse();
            var course3 = createBukakCourse();
            dbUtil.saveCourse(course1);
            dbUtil.saveCourse(course2);
            dbUtil.saveCourse(course3);
            var mapLatitude = 37.5172;
            var mapLongitude = 127.0276;
            var userLatitude = 37.5153291;
            var userLongitude = 127.1031347;
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 1000, null, null, null);

            var courses = sut.findNearbyCourses(condition, userLatitude, userLongitude);

            assertThat(course1.distanceFrom(new Coordinate(mapLatitude, mapLongitude)).value()).isLessThan(1000.0);
            assertThat(course2.distanceFrom(new Coordinate(mapLatitude, mapLongitude)).value()).isLessThan(1000.0);
            assertThat(course3.distanceFrom(new Coordinate(mapLatitude, mapLongitude)).value()).isGreaterThan(1000.0);

            assertThat(courses.courses()).hasSize(2)
                    .extracting(CourseResponse::name)
                    .containsExactlyInAnyOrder(course1.name().value(), course2.name().value());

            assertThat(courses.courses()).extracting(CourseResponse::distance).allMatch(Optional::isPresent);
            assertThat(courses.courses().get(0).distance().get().value()).isCloseTo(6640, Percentage.withPercentage(1));
            assertThat(courses.courses().get(1).distance().get().value()).isCloseTo(6583, Percentage.withPercentage(1));
        }

        @Test
        void 더_보여줄_코스가_없으면_hasNext_false() {
            var coordinates = CoordinateTestUtil.square(new Coordinate(37.5180, 127.0280), new Coordinate(37.5175, 127.0270));
            var courses = new ArrayList<Course>();
            for (var i = 0; i < 5; i++) courses.add(new Course(null, new CourseName("코스" + i), coordinates, ADMIN_USER));
            dbUtil.saveAllCourses(courses);
            var condition = new CourseFindCondition(37.5175, 127.0270, 3000, null, null, 0);

            var result = sut.findNearbyCourses(condition, null, null);

            assertThat(result.hasNext()).isFalse();
        }

        @Test
        void 더_보여줄_코스가_있으면_hasNext_true() {
            var coordinates = CoordinateTestUtil.square(new Coordinate(37.5180, 127.0280), new Coordinate(37.5175, 127.0270));
            var courses = new ArrayList<Course>();
            for (var i = 0; i < 15; i++) courses.add(new Course(null, new CourseName("코스" + i), coordinates, ADMIN_USER));
            dbUtil.saveAllCourses(courses);
            var condition = new CourseFindCondition(37.5175, 127.0270, 3000, null, null, 0);

            var result = sut.findNearbyCourses(condition, null, null);

            assertThat(result.hasNext()).isTrue();
        }

        @Test
        void 다음_데이터_존재_여부를_확인하며_코스_목록을_페이징_조회한다() {
            var coordinates = CoordinateTestUtil.square(new Coordinate(37.5180, 127.0280), new Coordinate(37.5175, 127.0270));
            var courses = new ArrayList<Course>();
            for (var i = 0; i < 15; i++) courses.add(new Course(null, new CourseName("코스" + i), coordinates, ADMIN_USER));
            dbUtil.saveAllCourses(courses);
            var condition = new CourseFindCondition(37.5175, 127.0270, 3000, null, null, 1);

            var result = sut.findNearbyCourses(condition, null, null);

            assertThat(result.hasNext()).isFalse();
            assertThat(result.courses().size()).isEqualTo(5);
        }

        @Test
        void 코스의_좌표_중에서_가장_가까운_좌표를_계산한다() {
            var coordinates = List.of(
                    new Coordinate(0, 0),
                    new Coordinate(0, 0.0001),
                    new Coordinate(0.0001, 0.0001),
                    new Coordinate(0.0001, 0),
                    new Coordinate(0, 0)
            );

            var course = createCourse("한강 러닝 코스", coordinates, ADMIN_USER);

            var insertCourse = dbUtil.saveCourse(course);

            var result = sut.findClosestCoordinate(insertCourse.id(), 0.0002, 0.0002);

            assertThat(result).isEqualTo(new Coordinate(0.0001, 0.0001));
        }

        @Test
        void 코스가_존재하지_않을_경우_예외가_발생한다() {
            assertThatThrownBy(() -> sut.findClosestCoordinate("notId", 0, 0))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class 코스_신고 {

        @Test
        void 동일한_유저는_코스_횟수에_카운트_하지_않는다() {
            var course1 = createHanRiverCourse(TEST_USER3);
            var course = dbUtil.saveCourse(course1);

            sut.reportCourse(course.id(), TEST_USER3.id());

            var result = dbUtil.findCourseById(course.id());
            assertThat(result.reportUserIds()).hasSize(1);
        }

        @Test
        void 두번_이하로_신고되면_알람이_안간다() {
            var course = createCourse("코스", 한강_좌표);
            var targetCourse = dbUtil.saveCourse(course);

            sut.reportCourse(targetCourse.id(), TEST_USER3.id());
            sut.reportCourse(targetCourse.id(), TEST_USER2.id());

            var result = dbUtil.findCourseById(targetCourse.id());
            assertThat(result.reportUserIds()).hasSize(2);
            verify(alerter, never()).alertCourse(any());
        }

        @Test
        void 세번_이상으로_신고되면_알람이_간다() {
            var course = createCourse("코스", 한강_좌표);
            var targetCourse = dbUtil.saveCourse(course);

            sut.reportCourse(targetCourse.id(), TEST_USER3.id());
            sut.reportCourse(targetCourse.id(), TEST_USER2.id());
            sut.reportCourse(targetCourse.id(), TEST_USER.id());

            var result = dbUtil.findCourseById(targetCourse.id());
            assertThat(result.reportUserIds()).hasSize(3);
            verify(alerter, times(1)).alertCourse(any());
        }
    }

    @Nested
    class 태그_재생성 {

        @Test
        void 리뷰가_있으면_태그를_생성하여_코스에_저장한다() {
            var course = createCourse("코스", 한강_좌표);
            course.reviews().add(new Review(ADMIN_USER, "야경이 멋집니다", 5));
            var saved = dbUtil.saveCourse(course);

            when(courseTagGenerator.generate(any())).thenReturn(List.of(CourseTag.NIGHT_VIEW, CourseTag.FLAT));

            sut.regenerateTags(saved.id());

            var result = dbUtil.findCourseById(saved.id());
            assertThat(result.tags()).containsExactly(CourseTag.NIGHT_VIEW, CourseTag.FLAT);
            verify(courseTagGenerator, times(1)).generate(any());
        }

        @Test
        void 리뷰가_없으면_태그를_생성하지_않는다() {
            var course = createCourse("코스", 한강_좌표);
            var saved = dbUtil.saveCourse(course);

            sut.regenerateTags(saved.id());

            var result = dbUtil.findCourseById(saved.id());
            assertThat(result.tags()).isEmpty();
            verify(courseTagGenerator, never()).generate(any());
        }
    }

    @Nested
    class 나의_코스_조회 {

        @Test
        void 내가_만든_코스만_조회된다() {
            dbUtil.saveCourse(createCourse("내 코스1", 한강_좌표, TEST_USER));
            dbUtil.saveCourse(createCourse("내 코스2", 한강_좌표, TEST_USER));
            dbUtil.saveCourse(createCourse("남의 코스", 한강_좌표, TEST_USER2));

            var result = sut.findCustomCourses(TEST_USER.id(), null, null);

            assertThat(result.courses())
                    .hasSize(2)
                    .extracting(CourseResponse::name)
                    .containsExactlyInAnyOrder("내 코스1", "내 코스2");
        }

        @Test
        void 내가_만든_코스가_없으면_빈_리스트를_반환한다() {
            dbUtil.saveCourse(createCourse("남의 코스", 한강_좌표, TEST_USER2));

            var result = sut.findCustomCourses(TEST_USER.id(), null, null);

            assertThat(result.courses()).isEmpty();
        }

        @Test
        void 최신순으로_정렬된다() {
            var now = Instant.now();
            var oldCourse = createCourse("오래된 코스", 한강_좌표, TEST_USER);
            var newCourse = createCourse("최신 코스", 한강_좌표, TEST_USER);

            ReflectionTestUtils.setField(oldCourse, "createdAt", now);
            ReflectionTestUtils.setField(newCourse, "createdAt", now.plusSeconds(1L));

            dbUtil.saveCourse(oldCourse);
            dbUtil.saveCourse(newCourse);

            var result = sut.findCustomCourses(TEST_USER.id(), null, null);

            assertThat(result.courses())
                    .extracting(CourseResponse::name)
                    .containsExactly("최신 코스", "오래된 코스");
        }
    }

    @Nested
    class 유저_코스_생성 {

        @Test
        void 유저가_생성한_코스를_저장한다() {

            var name = "나만의 코스1";
            sut.addCustomCourse(name, 한강_좌표, TEST_USER.id());

            var result = dbUtil.findCourseByName(name);
            assertThat(result.name().value()).isEqualTo(name);
        }


        @Test
        void 유저가_존재하지_않을_경우_예외를_던진다() {
            var name = "나만의 코스1";
            assertThatThrownBy(() -> sut.addCustomCourse(name, 한강_좌표, "userid"))
                    .isInstanceOf(UnauthorizedException.class);

        }

        @ParameterizedTest
        @ValueSource(strings = {
                "나만의 코스",
                " 나만의 코스   "
        })
        void 앞_뒤_공백을_제거하여_코스를_생성한다(String name) {
            var expectedName = "나만의 코스";

            sut.addCustomCourse(name, 한강_좌표, TEST_USER.id());

            var result = dbUtil.findCourseByName(expectedName);
            assertThat(result.name().value()).isEqualTo(expectedName);
        }
    }

    @Nested
    class 파일_코스_임포트 {

        @Test
        void 파일을_통해_여러_코스를_한번에_저장한다() {
            var content = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <gpx version="1.1" creator="Coursepick" xmlns="http://www.topografix.com/GPX/1/1">
                        <trk>
                            <name>코스1</name>
                            <trkseg>
                                <trkpt lat="37.48" lon="126.92"/>
                                <trkpt lat="37.49" lon="126.93"/>
                            </trkseg>
                        </trk>
                        <trk>
                            <name>코스2</name>
                            <trkseg>
                                <trkpt lat="37.5" lon="127.0"/>
                                <trkpt lat="37.51" lon="127.1"/>
                            </trkseg>
                        </trk>
                    </gpx>
                    """;
            var file = new org.springframework.mock.web.MockMultipartFile("file", "test.gpx", "application/gpx+xml", content.getBytes());

            var response = sut.importCustomCourseFile(file, TEST_USER.id());

            assertThat(response.successCount()).isEqualTo(2);
            assertThat(response.successNames()).containsExactlyInAnyOrder("코스1", "코스2");
            assertThat(dbUtil.findCourseByName("코스1")).isNotNull();
            assertThat(dbUtil.findCourseByName("코스2")).isNotNull();
        }

        @Test
        void 이름이_중복된_코스는_건너뛰고_나머지만_저장한다() {
            // Given
            sut.addCustomCourse("이미있는코스", 한강_좌표, TEST_USER.id());

            var content = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <gpx version="1.1" creator="Coursepick" xmlns="http://www.topografix.com/GPX/1/1">
                        <trk>
                            <name>이미있는코스</name>
                            <trkseg>
                                <trkpt lat="37.4" lon="126.9"/>
                                <trkpt lat="37.41" lon="126.91"/>
                            </trkseg>
                        </trk>
                        <trk>
                            <name>새로운코스</name>
                            <trkseg>
                                <trkpt lat="37.5" lon="127.0"/>
                                <trkpt lat="37.51" lon="127.1"/>
                            </trkseg>
                        </trk>
                    </gpx>
                    """;
            var file = new org.springframework.mock.web.MockMultipartFile("file", "test.gpx", "application/gpx+xml", content.getBytes());

            // When
            var response = sut.importCustomCourseFile(file, TEST_USER.id());

            // Then
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(response.successNames()).containsExactly("새로운코스");
            assertThat(response.skippedCount()).isEqualTo(1);
            assertThat(response.skippedReasons().get(0)).contains("중복된 이름");
            assertThat(dbUtil.findCourseByName("새로운코스")).isNotNull();
        }

        @Test
        void 이름이_없는_트랙은_제외하고_저장한다() {
            // Given
            var content = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <gpx version="1.1" creator="Coursepick" xmlns="http://www.topografix.com/GPX/1/1">
                        <trk>
                            <name>정상코스</name>
                            <trkseg>
                                <trkpt lat="37.4" lon="126.9"/>
                                <trkpt lat="37.41" lon="126.91"/>
                            </trkseg>
                        </trk>
                        <trk>
                            <trkseg>
                                <trkpt lat="37.5" lon="127.0"/>
                                <trkpt lat="37.51" lon="127.1"/>
                            </trkseg>
                        </trk>
                    </gpx>
                    """;
            var file = new org.springframework.mock.web.MockMultipartFile("file", "test.gpx", "application/gpx+xml", content.getBytes());

            // When
            var response = sut.importCustomCourseFile(file, TEST_USER.id());

            // Then
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(response.successNames()).containsExactly("정상코스");
            assertThat(response.skippedCount()).isEqualTo(1);
            assertThat(response.skippedReasons().get(0)).contains("이름 누락");
        }
    }

    @Nested
    class 리뷰_추가_삭제_신고 {

        private String courseId;
        private String reviewId;

        @BeforeEach
        void setUpNested() {
            var course = createBukakCourse(TEST_USER);
            courseId = dbUtil.saveCourse(course).id();
        }

        @Test
        void 리뷰를_추가하면_DB에_저장된다() {
            sut.addReview(courseId, TEST_USER.id(), "좋은 코스입니다", 4);

            var result = dbUtil.findCourseById(courseId);
            assertThat(result.reviews()).hasSize(1);
            assertThat(result.reviews().getFirst().userId()).isEqualTo(TEST_USER.id());
            assertThat(result.reviews().getFirst().content()).isEqualTo("좋은 코스입니다");
            assertThat(result.reviews().getFirst().rating()).isEqualTo(4);
        }

        @Test
        void 존재하지_않는_코스에_리뷰를_추가하면_예외가_발생한다() {
            assertThatThrownBy(() -> sut.addReview("notExistCourseId", TEST_USER.id(), "좋은 코스입니다", 4))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 리뷰를_삭제하면_DB에서_제거된다() {
            sut.addReview(courseId, TEST_USER.id(), "좋은 코스입니다", 5);
            reviewId = dbUtil.findCourseById(courseId).reviews().getFirst().id();

            sut.deleteReview(courseId, reviewId, TEST_USER.id());

            var result = dbUtil.findCourseById(courseId);
            assertThat(result.reviews()).isEmpty();
        }

        @Test
        void 존재하지_않는_코스의_리뷰를_삭제하면_예외가_발생한다() {
            sut.addReview(courseId, TEST_USER.id(), "좋은 코스입니다", 5);
            reviewId = dbUtil.findCourseById(courseId).reviews().getFirst().id();

            assertThatThrownBy(() -> sut.deleteReview("notExistCourseId", reviewId, TEST_USER.id()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 존재하지_않는_리뷰를_삭제하면_예외가_발생한다() {
            assertThatThrownBy(() -> sut.deleteReview(courseId, "notExistReviewId", TEST_USER.id()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 리뷰를_신고하면_알람이_간다() {
            sut.addReview(courseId, TEST_USER.id(), "좋은 코스입니다", 5);
            reviewId = dbUtil.findCourseById(courseId).reviews().getFirst().id();

            sut.reportReview(courseId, reviewId, TEST_USER3.id());

            verify(alerter, times(1)).alertReview(any(), any());
        }

        @Test
        void 리뷰를_여러_번_신고하면_알람이_여러_번_간다() {
            sut.addReview(courseId, TEST_USER.id(), "좋은 코스입니다", 5);
            reviewId = dbUtil.findCourseById(courseId).reviews().getFirst().id();

            sut.reportReview(courseId, reviewId, TEST_USER3.id());
            sut.reportReview(courseId, reviewId, TEST_USER2.id());

            verify(alerter, times(2)).alertReview(any(), any());
        }
    }
}
