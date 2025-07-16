package coursepick.coursepick.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CourseTest {

    @Nested
    class 생성_테스트 {

        private static final String LENGTH_31_NAME = "1234567890123456789012345678901";

        @Test
        void 코스를_생성한다() {
            assertThatCode(() -> new Course("코스이름", getNormalCoordinates()))
                    .doesNotThrowAnyException();
        }

        @Test
        void 앞_뒤_공백을_제거하여_생성한다() {
            Course course = new Course(" 코스이름   ", getNormalCoordinates());
            assertThat(course.name()).isEqualTo("코스이름");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                LENGTH_31_NAME,
                "짧"
        })
        void 잘못된_길이의_이름으로_코스를_생성하면_예외가_발생한다(String name) {
            assertThatThrownBy(() -> new Course(name, getNormalCoordinates()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "코스 이름",
                "코스  이름",
                "코스   이름",
                "코스    이름",
        })
        void 이름의_연속공백을_한_칸으로_변환하여_코스를_생성한다(String name) {
            Course course = new Course(name, getNormalCoordinates());
            assertThat(course.name()).isEqualTo("코스 이름");
        }

        @Test
        void 코스의_좌표의_개수가_2보다_적으면_예외가_발생한다() {
            assertThatThrownBy(() -> new Course("코스이름", List.of(new Coordinate(1d, 1d))))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 첫_좌표와_끝_좌표의_위도경도가_동일하지_않으면_예외가_발생한다() {
            assertThatThrownBy(() -> new Course("코스이름", List.of(new Coordinate(1d, 1d), new Coordinate(2d, 3d))))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        private static List<Coordinate> getNormalCoordinates() {
            return List.of(new Coordinate(1d, 1d), new Coordinate(1d, 1d));
        }
    }

    @Test
    void 코스의_총_거리를_계산할_수_있다() {
        Course course = new Course("한강뛰어보자", List.of(
                new Coordinate(37.518400, 126.995600),
                new Coordinate(37.518000, 126.996500),
                new Coordinate(37.517500, 126.998000),
                new Coordinate(37.517000, 127.000000),
                new Coordinate(37.516500, 127.002000),
                new Coordinate(37.516000, 127.004500),
                new Coordinate(37.515500, 127.007000),
                new Coordinate(37.515000, 127.009500),
                new Coordinate(37.515500, 127.007000),
                new Coordinate(37.516000, 127.004500),
                new Coordinate(37.516500, 127.002000),
                new Coordinate(37.517000, 127.000000),
                new Coordinate(37.517500, 126.998000),
                new Coordinate(37.518000, 126.996500),
                new Coordinate(37.518400, 126.995600)
        ));

        double totalLength = course.length();

        assertThat((int) totalLength).isEqualTo(2573);
    }

    @ParameterizedTest
    @CsvSource({
            "37.517712, 126.995012, 142",
            "37.516678, 126.997065, 46"
    })
    void 특정_좌표에서_코스까지_가장_가까운_거리를_계산할_수_있다(double latitude, double longitude, int expectedDistance) {
        Course course = new Course("한강뛰어보자", List.of(
                new Coordinate(37.519760, 126.995477),
                new Coordinate(37.517083, 126.997182),
                new Coordinate(37.519760, 126.995477)
        ));
        Coordinate target = new Coordinate(latitude, longitude);

        double distance = course.minDistanceFrom(target);

        assertThat((int) distance).isEqualTo(expectedDistance);
    }
}
