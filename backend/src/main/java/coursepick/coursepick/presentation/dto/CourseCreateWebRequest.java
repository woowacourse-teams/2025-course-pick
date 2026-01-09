package coursepick.coursepick.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "코스 생성 요청")
public record CourseCreateWebRequest(
        @Schema(description = "코스 이름", example = "한강 러닝 코스", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(
                description = "도로 타입 (트랙, 트레일, 보도, 알수없음)",
                example = "트레일",
                allowableValues = {"트랙", "트레일", "보도", "알수없음"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String roadType,

        @Schema(
                description = "난이도 (쉬움, 보통, 어려움)",
                example = "보통",
                allowableValues = {"쉬움", "보통", "어려움"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String difficulty,

        @Schema(description = "코스를 구성하는 좌표 목록 (최소 2개 이상)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        List<CoordinateWebRequest> coordinates
) {
}
