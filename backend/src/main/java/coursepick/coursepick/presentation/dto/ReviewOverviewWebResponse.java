package coursepick.coursepick.presentation.dto;

public record ReviewOverviewWebResponse(
        int reviewCount,
        double averageRating
) {
    public static ReviewOverviewWebResponse from(int reviewCount, double averageRating) {
        return new ReviewOverviewWebResponse(reviewCount, averageRating);
    }
}
