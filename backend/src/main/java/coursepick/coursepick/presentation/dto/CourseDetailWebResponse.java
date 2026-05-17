package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CourseDetailResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CourseDetailWebResponse(
        @Schema(description = "코스 ID", example = "689c3143182cecc6353cca7b")
        String id,
        @Schema(description = "코스 이름", example = "석촌호수")
        String name,
        @Schema(description = "코스 전체 길이 (미터)", example = "2146.123")
        double length,
        @Schema(description = "코스를 구성하는 좌표 목록")
        List<CoordinateWebResponse> coordinates,
        @Schema(description = "리뷰 평균 몇점 및 리뷰 개수")
        ReviewOverviewWebResponse reviewOverview,
        @Schema(description = "코스 리뷰 목록")
        List<ReviewWebResponse> reviews,
        @Schema(description = "코스 태그 목록 (최대 5개)")
        List<CourseTagWebResponse> tags
) {
    public static CourseDetailWebResponse from(CourseDetailResponse response) {
        return new CourseDetailWebResponse(
                response.id(),
                response.name(),
                response.length().value(),
                CoordinateWebResponse.from(response.coordinates()),
                ReviewOverviewWebResponse.from(response.reviewCount(), response.averageRating()),
                ReviewWebResponse.from(response.reviews()),
                CourseTagWebResponse.from(response.tags())
        );
    }
}
