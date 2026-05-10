package coursepick.coursepick.domain.course;

public record Rating(
        int reviewCount,
        int totalRating,
        double averageRating
) {

    public static Rating addReviewRating(Rating oldReview, int newRating) {
        int newReviewCount = oldReview.reviewCount + 1;
        int newTotalRating = oldReview.totalRating + newRating;

        double calculatedAverage = (double) newTotalRating / newReviewCount;

        double averageRating = Math.round(calculatedAverage * 10) / 10.0;

        return new Rating(newReviewCount, newTotalRating, averageRating);
    }
}
