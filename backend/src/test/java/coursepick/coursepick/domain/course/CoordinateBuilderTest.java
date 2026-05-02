package coursepick.coursepick.domain.course;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinateBuilderTest {

    @Test
    void Douglas_Peucker_알고리즘을_사용하여_경로를_단순화한다() {
        // 직선상에 있는 점들과 튀는 점 하나
        List<Coordinate> coordinates = List.of(
                new Coordinate(0, 0),
                new Coordinate(0.00001, 0.00001), // 직선에 가까움
                new Coordinate(0.00005, 0.00005), // 직선에 가까움
                new Coordinate(0.0001, 0.0001),   // 직선에 가까움
                new Coordinate(0.0002, 0.0005),   // 멀리 뜀 (특징점)
                new Coordinate(0.0003, 0.0003),   // 다시 직선 근처
                new Coordinate(0.0005, 0.0005)    // 마지막 점
        );

        CoordinateBuilder builder = CoordinateBuilder.fromCoordinates(coordinates);

        // 10m 오차로 단순화
        List<Coordinate> simplified = builder.simplify(new Meter(10)).build();

        // 점의 개수가 줄어들어야 함
        assertThat(simplified.size()).isLessThan(coordinates.size());
        assertThat(simplified.getFirst()).isEqualTo(new Coordinate(0, 0));
        assertThat(simplified.getLast()).isEqualTo(new Coordinate(0.0005, 0.0005));
    }

    @Test
    void 직선_경로의_경우_첫_점과_끝_점만_남는다() {
        List<Coordinate> coordinates = List.of(
                new Coordinate(37.5, 127.0),
                new Coordinate(37.51, 127.01),
                new Coordinate(37.52, 127.02),
                new Coordinate(37.53, 127.03),
                new Coordinate(37.54, 127.04),
                new Coordinate(37.55, 127.05)
        );

        CoordinateBuilder builder = CoordinateBuilder.fromCoordinates(coordinates);

        // 아주 작은 오차로 단순화해도 직선이면 점이 많이 제거됨
        List<Coordinate> simplified = builder.simplify(new Meter(1)).build();

        // 첫 점과 끝 점만 남음
        assertThat(simplified).hasSize(2);
        assertThat(simplified.getFirst()).isEqualTo(new Coordinate(37.5, 127.0));
        assertThat(simplified.getLast()).isEqualTo(new Coordinate(37.55, 127.05));
    }
}
