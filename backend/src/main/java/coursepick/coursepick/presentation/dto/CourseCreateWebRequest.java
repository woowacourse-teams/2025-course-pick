package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CourseCreateWebRequest(
        @Schema(description = "코스 이름", example = "한강 러닝 코스", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        String name,

        @Schema(description = "코스를 구성하는 좌표 목록 (최소 2개 이상)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Size(min = 2, message = "코스는 출발지와 도착지 등 최소 2개 이상의 좌표가 필요합니다.")
        List<
            @NotNull(message = "개별 좌표 값은 비어있을 수 없습니다.") 
            @Size(min = 2, max = 2, message = "각 좌표는 반드시 [위도, 경도] 2개의 값을 가져야 합니다.")
            List<Double>
        > coordinates
) {

    public List<Coordinate> toCoordinates() {
        return coordinates.stream()
                .map(raw -> new Coordinate(raw.getFirst(), raw.get(1)))
                .toList();
    }
}

