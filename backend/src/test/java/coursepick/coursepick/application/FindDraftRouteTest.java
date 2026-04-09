package coursepick.coursepick.application;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.CourseRepository;
import coursepick.coursepick.domain.course.DraftSegment;
import coursepick.coursepick.domain.course.RouteFinder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static coursepick.coursepick.test_util.CoordinateTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindDraftRouteTest {

    CourseRepository courseRepository = mock(CourseRepository.class);
    RouteFinder routeFinder = mock(RouteFinder.class);
    CourseApplicationService sut = new CourseApplicationService(courseRepository, routeFinder);

    @Test
    void 웨이포인트_두_개로_경로를_조회한다() {
        var start = new Coordinate(0, 0);
        var end = right(start, 1000);
        when(routeFinder.find(start, end)).thenReturn(List.of(start, end));

        DraftSegment result = sut.findDraftRoute(List.of(start, end));

        assertThat(result.coordinates()).containsExactly(start, end);
        assertThat(result.length().value()).isCloseTo(1000, withPercentage(1));
    }

    @Test
    void 웨이포인트_세_개로_경로를_조회하면_구간이_합산된다() {
        var start = new Coordinate(0, 0);
        var mid = right(start, 1000);
        var end = right(mid, 1000);
        when(routeFinder.find(start, mid)).thenReturn(List.of(start, mid));
        when(routeFinder.find(mid, end)).thenReturn(List.of(mid, end));

        DraftSegment result = sut.findDraftRoute(List.of(start, mid, end));

        assertThat(result.coordinates()).containsExactly(start, mid, mid, end);
        assertThat(result.length().value()).isCloseTo(2000, withPercentage(1));
    }
}
