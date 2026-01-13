package coursepick.coursepick.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "코스 생성 요청")
public record CourseCreateWebRequest(
        @Schema(description = "코스 이름", example = "한강 러닝 코스", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "코스를 구성하는 좌표 목록 (최소 2개 이상)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        List<CoordinateWebRequest> coordinates
) {
}
