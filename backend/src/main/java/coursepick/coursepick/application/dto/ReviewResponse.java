package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.course.Review;

import java.util.List;

public record ReviewResponse(
        String authorNickname,
        String content,
        int rating
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(review.authorNickname(), review.content(), review.rating());
    }

    public static List<ReviewResponse> from(List<Review> reviews) {
        return reviews.stream()
                .map(ReviewResponse::from)
                .toList();
    }
}
