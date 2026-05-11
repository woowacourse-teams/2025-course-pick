package coursepick.coursepick.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewOverviewWebResponse(
        @Schema(description = "리뷰 수", example = "42")
        int reviewCount,
        @Schema(description = "코스 평균 별점 (소수점 1자리)", example = "4.3")
        double averageRating
) {
    public static ReviewOverviewWebResponse from(int reviewCount, double averageRating) {
        return new ReviewOverviewWebResponse(reviewCount, averageRating);
    }
}
