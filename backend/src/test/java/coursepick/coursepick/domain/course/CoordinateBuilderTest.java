package coursepick.coursepick.domain.course;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinateBuilderTest {

    @Test
    @DisplayName("Douglas-Peucker 알고리즘을 사용하여 경로를 단순화할 수 있다.")
    void simplifyTest() {
        // Given: 직선상에 있는 점들과 튀는 점 하나
        List<Coordinate> coordinates = List.of(
                new Coordinate(0, 0),
                new Coordinate(0.00001, 0.00001), // 직선에 가까움
                new Coordinate(0.00005, 0.00005), // 직선에 가까움
                new Coordinate(0.0001, 0.0001),   // 직선에 가까움
                new Coordinate(0.0002, 0.0005),   // 멀리 뜀 (특징점)
                new Coordinate(0.0003, 0.0003),   // 다시 직선 근처
                new Coordinate(0.0005, 0.0005)    // 마지막 점
        );

        CoordinateBuilder builder = CoordinateBuilder.fromRawCoordinates(coordinates);

        // When: 10m 오차로 단순화
        List<Coordinate> simplified = builder.simplify(new Meter(10)).build();

        // Then: 점의 개수가 줄어들어야 함
        assertThat(simplified.size()).isLessThan(coordinates.size());
        assertThat(simplified.getFirst()).isEqualTo(new Coordinate(0, 0));
        assertThat(simplified.getLast()).isEqualTo(new Coordinate(0.0005, 0.0005));
    }

    @Test
    @DisplayName("직선 경로의 경우 첫 점과 끝 점만 남는다.")
    void simplifyStraightLineTest() {
        // Given: 완벽한 직선상에 있는 점들
        List<Coordinate> coordinates = List.of(
                new Coordinate(37.5, 127.0),
                new Coordinate(37.51, 127.01),
                new Coordinate(37.52, 127.02),
                new Coordinate(37.53, 127.03),
                new Coordinate(37.54, 127.04),
                new Coordinate(37.55, 127.05)
        );

        CoordinateBuilder builder = CoordinateBuilder.fromRawCoordinates(coordinates);

        // When: 아주 작은 오차로 단순화해도 직선이면 점이 많이 제거됨
        List<Coordinate> simplified = builder.simplify(new Meter(1)).build();

        // Then: 첫 점과 끝 점만 남음
        assertThat(simplified).hasSize(2);
        assertThat(simplified.getFirst()).isEqualTo(new Coordinate(37.5, 127.0));
        assertThat(simplified.getLast()).isEqualTo(new Coordinate(37.55, 127.05));
    }
}
