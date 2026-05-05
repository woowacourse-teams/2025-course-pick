package coursepick.coursepick.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateReviewWebRequest(
        @Schema(description = "리뷰 내용 (1~500자)", example = "노을이 예쁜 코스입니다")
        String content
) {
}
