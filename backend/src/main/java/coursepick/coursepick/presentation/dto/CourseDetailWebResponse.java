package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CourseDetailResponse;
import coursepick.coursepick.application.dto.ReviewResponse;
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
        List<ReviewWebResponse> reviews
) {
    public static CourseDetailWebResponse from(CourseDetailResponse response) {
        return new CourseDetailWebResponse(
                response.id(),
                response.name(),
                response.length().value(),
                CoordinateWebResponse.from(response.coordinates()),
                ReviewOverviewWebResponse.from(response.reviewCount(), calculateAverageRating(response.reviews())),
                ReviewWebResponse.from(response.reviews())
        );
    }

    private static double calculateAverageRating(List<ReviewResponse> reviews) {
        if (reviews.isEmpty()) return 0.0;
        int total = reviews.stream().mapToInt(ReviewResponse::rating).sum();
        return Math.round((double) total / reviews.size() * 10) / 10.0;
    }
}
