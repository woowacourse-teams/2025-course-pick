package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CourseDetailResponse;

import java.util.List;

public record CourseDetailWebResponse(
        String id,
        String name,
        double length,
        List<CoordinateWebResponse> coordinates,
        ReviewOverviewWebResponse reviewOverview,
        List<ReviewWebResponse> reviews,
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
