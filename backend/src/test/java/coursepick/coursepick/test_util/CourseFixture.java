package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseName;
import coursepick.coursepick.domain.user.User;

import java.util.List;

import static coursepick.coursepick.test_util.UserFixture.ADMIN_USER;

public abstract class CourseFixture {

    public static final List<Coordinate> 한강_좌표 = List.of(
            new Coordinate(37.5180, 127.0280),
            new Coordinate(37.5175, 127.0270),
            new Coordinate(37.5170, 127.0265),
            new Coordinate(37.5180, 127.0280)
    );

    public static final List<Coordinate> 양재천_좌표 = List.of(
            new Coordinate(37.5165, 127.0285),
            new Coordinate(37.5160, 127.0278),
            new Coordinate(37.5155, 127.0265),
            new Coordinate(37.5165, 127.0285)
    );

    public static final List<Coordinate> 북악산_좌표 = List.of(
            new Coordinate(37.602500, 126.967000),
            new Coordinate(37.603000, 126.968000),
            new Coordinate(37.603500, 126.969000),
            new Coordinate(37.602500, 126.967000)
    );

    public static Course createHanRiverCourse() {
        return createHanRiverCourse(ADMIN_USER);
    }

    public static Course createHanRiverCourse(User user) {
        return new Course(null, new CourseName("한강 러닝 코스"), 한강_좌표, user);
    }

    public static Course createYangjaeCourse() {
        return createYangjaeCourse(ADMIN_USER);
    }

    public static Course createYangjaeCourse(User user) {
        return new Course(null, new CourseName("양재천 산책길"), 양재천_좌표, user);
    }

    public static Course createBukakCourse() {
        return createBukakCourse(ADMIN_USER);
    }

    public static Course createBukakCourse(User user) {
        return new Course(null, new CourseName("북악산 둘레길"), 북악산_좌표, user);
    }

    public static Course createCourse(String name, List<Coordinate> coordinates) {
        return createCourse(name, coordinates, ADMIN_USER);
    }

    public static Course createCourse(String name, List<Coordinate> coordinates, User user) {
        return new Course(null, new CourseName(name), coordinates, user);
    }

    public static Course createSimpleCourse() {
        return createCourse("테스트 코스", List.of(new Coordinate(0, 0), new Coordinate(1, 1)));
    }
}
