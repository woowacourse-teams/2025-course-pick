package coursepick.coursepick.infrastructure.geocoding;

import coursepick.coursepick.domain.course.Coordinate;

import java.util.Optional;

public interface ReverseGeocoder {

    /**
     * 좌표를 행정구역 이름(예: "서초구 반포4동")으로 변환합니다.
     * 변환할 수 없으면 빈 값을 반환합니다.
     */
    Optional<String> findRegionName(Coordinate coordinate);
}
