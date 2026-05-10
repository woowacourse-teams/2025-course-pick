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
        @Schema(description = "코스 별점 정보")
        RatingWebResponse rating,
        @Schema(description = "코스 리뷰 목록")
        List<ReviewWebResponse> reviews
) {
    public static CourseDetailWebResponse from(CourseDetailResponse response) {
        return new CourseDetailWebResponse(
                response.id(),
                response.name(),
                response.length().value(),
                CoordinateWebResponse.from(response.coordinates()),
                RatingWebResponse.from(response.reviewCount(), response.averageRating()),
                ReviewWebResponse.from(response.reviews())
        );
    }
}
