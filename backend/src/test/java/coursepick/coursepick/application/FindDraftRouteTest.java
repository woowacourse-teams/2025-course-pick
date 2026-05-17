package coursepick.coursepick.application;

import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.domain.user.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static coursepick.coursepick.test_util.CoordinateTestUtil.right;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindDraftRouteTest {

    CourseRepository courseRepository = mock(CourseRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    RouteFinder routeFinder = mock(RouteFinder.class);
    CourseApplicationService courseService = new CourseApplicationService(courseRepository, userRepository, routeFinder, null, null, null);

    @Test
    void 경로_좌표가_1개이면_예외가_발생한다() {
        var coordinate = new Coordinate(0, 0);

        assertThatThrownBy(() -> courseService.findDraftRoute(List.of(coordinate)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 경로_좌표_두_개로_경로를_조회할_때_원본_첫_끝_좌표는_제외되고_보정된_경로가_반환된다() {
        // 사용자가 도로 밖을 탭한 원본 좌표
        var rawStart = new Coordinate(0, 0);
        var rawEnd = right(rawStart, 1000);

        // OsrmRouteFinder: OSRM 보정 경로 앞뒤에 원본(raw) 좌표를 붙여서 반환한다
        var snappedStart = right(rawStart, 50);  // OSRM이 도로에 보정한 시작 좌표
        var midPoint = right(rawStart, 500);
        var snappedEnd = right(rawStart, 950);   // OSRM이 도로에 보정한 끝 좌표
        when(routeFinder.find(rawStart, rawEnd))
                .thenReturn(List.of(rawStart, snappedStart, midPoint, snappedEnd, rawEnd));

        DraftSegment result = courseService.findDraftRoute(List.of(rawStart, rawEnd));

        // 원본(raw) 첫/마지막 좌표는 제외되고, 보정된 좌표로 시작/끝난다
        assertThat(result.coordinates()).containsExactly(snappedStart, midPoint, snappedEnd);

        //snappedStart부터 midPoint까지(450m), midPoint부터 snappedEnd까지(450m) = 900m
        assertThat(result.length().value()).isCloseTo(900, withPercentage(1));
    }

    @Test
    void 경로_좌표_세_개로_경로를_조회할_때_두_구간이_중복_없이_병합된다() {
        // 사용자가 입력하는 좌표: rawStart -> rawMid -> rawEnd (3개, 구간 2개)
        var rawStart = new Coordinate(0, 0);
        var rawMid = right(rawStart, 1000);
        var rawEnd = right(rawMid, 1000);

        // 각 구간은 OsrmRouteFinder처럼 원본 좌표를 앞뒤에 붙여서 반환
        var snappedStart = right(rawStart, 50); // 보정된 시작 좌표
        var midPoint1 = right(rawStart, 300);
        var midPoint2 = right(rawMid, 300);
        var snappedEnd = right(rawMid, 950); // 보정된 끝 좌표
        when(routeFinder.find(rawStart, rawMid))
                .thenReturn(List.of(rawStart, snappedStart, midPoint1, rawMid));
        when(routeFinder.find(rawMid, rawEnd))
                .thenReturn(List.of(rawMid, midPoint2, snappedEnd, rawEnd));

        DraftSegment result = courseService.findDraftRoute(List.of(rawStart, rawMid, rawEnd));

        // 원본 첫/끝 좌표 제외, 중간 웨이포인트(rawMid)는 연결점으로 유지되며 중복 없이 병합된다
        assertThat(result.coordinates()).containsExactly(snappedStart, midPoint1, rawMid, midPoint2, snappedEnd);
        // snappedStart부터 midPoint1까지(250m) + midPoint1부터 rawMid까지(700m) + rawMid부터 midPoint2까지(300m) + midPoint2부터 snappedEnd까지(650m) = 1900m
        assertThat(result.length().value()).isCloseTo(1900, withPercentage(1));
    }
}
