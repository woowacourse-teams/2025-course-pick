package coursepick.coursepick.application;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.CourseRepository;
import coursepick.coursepick.domain.course.DraftSegment;
import coursepick.coursepick.domain.course.RouteFinder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static coursepick.coursepick.test_util.CoordinateTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindDraftRouteTest {

    CourseRepository courseRepository = mock(CourseRepository.class);
    RouteFinder routeFinder = mock(RouteFinder.class);
    CourseApplicationService courseService = new CourseApplicationService(courseRepository, routeFinder);

    @Test
    void 경로_좌표가_1개이면_예외가_발생한다() {
        var coordinate = new Coordinate(0, 0);

        assertThatThrownBy(() -> courseService.findDraftRoute(List.of(coordinate)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 경로_좌표_두_개로_경로를_조회할_때_반환된_경로에_중간_좌표까지_모두_포함된다() {
        // 사용자가 입력하는 좌표: start -> end (2개)
        var start = new Coordinate(0, 0);
        var end = right(start, 1000);

        // routeFinder가 실제 도로 경로를 탐색하면 중간 좌표도 함께 반환된다.
        var midPoint1 = right(start, 300);
        var midPoint2 = right(start, 700);
        when(routeFinder.find(start, end)).thenReturn(List.of(start, midPoint1, midPoint2, end));

        DraftSegment result = courseService.findDraftRoute(List.of(start, end));

        assertThat(result.coordinates()).containsExactly(start, midPoint1, midPoint2, end);
        assertThat(result.length().value()).isCloseTo(1000, withPercentage(1));
    }

    @Test
    void 경로_좌표_세_개로_경로를_조회할_때_두_구간이_중복_없이_병합된다() {
        // 사용자가 입력하는 좌표: start -> mid -> end (3개, 구간 2개)
        var start = new Coordinate(0, 0);
        var mid = right(start, 1000);
        var end = right(mid, 1000);

        // 각 구간별로 routeFinder가 중간 좌표를 포함한 경로를 반환
        var midPoint1 = right(start, 300);  // start -> mid 구간의 중간 좌표
        var midPoint2 = right(mid, 300);    // mid -> end 구간의 중간 좌표
        when(routeFinder.find(start, mid)).thenReturn(List.of(start, midPoint1, mid));
        when(routeFinder.find(mid, end)).thenReturn(List.of(mid, midPoint2, end));

        DraftSegment result = courseService.findDraftRoute(List.of(start, mid, end));

        // 두 구간이 하나로 합쳐지고, 연결점(mid)이 중복되지 않는다.
        assertThat(result.coordinates()).containsExactly(start, midPoint1, mid, midPoint2, end);
        assertThat(result.length().value()).isCloseTo(2000, withPercentage(1));
    }
}
