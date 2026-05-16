package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CourseCreateWebRequest(
        @NotNull
        String name,

        @NotNull(message = "좌표는 비어있을 수 없습니다.")
        @Size(min = 2, message = "코스는 출발지와 도착지 등 최소 2개 이상의 좌표가 필요합니다.")
        List<@NotNull(message = "개별 좌표 값은 비어있을 수 없습니다.") @Valid CoordinateWebRequest> coordinates
) {

    public List<Coordinate> toCoordinates() {
        return coordinates.stream()
                .map(CoordinateWebRequest::toCoordinate)
                .toList();
    }
}

