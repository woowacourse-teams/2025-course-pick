package coursepick.coursepick.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineCourseTest {

    @Test
    void 코스_외부_멀리_떨어진_점에서_코스까지의_거리를_계산한다() {
        var course = new LineCourse("작은원형코스", List.of(
                new Coordinate(37.5, 127.0),
                new Coordinate(37.5001, 127.0),
                new Coordinate(37.5, 127.0001),
                new Coordinate(37.4999, 127.0)
        ));
        var target = new Coordinate(37.52, 127.02); // 매우 멀리 떨어진 점

        var distance = course.distanceFrom(target);

        assertThat((int) distance.value()).isEqualTo(2838);
    }

    @Test
    void 코스_내부의_점에서_코스까지의_거리를_계산한다() {
        var course = new LineCourse("사각형코스", List.of(
                new Coordinate(37.5, 127.0),
                new Coordinate(37.501, 127.0),
                new Coordinate(37.501, 127.001),
                new Coordinate(37.5, 127.001)
        ));
        var target = new Coordinate(37.501, 127.0);

        var distance = course.distanceFrom(target);

        assertThat((int) distance.value()).isEqualTo(111);
    }
}
