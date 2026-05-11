package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.course.Review;

import java.util.List;

public record ReviewResponse(
        String id,
        String authorNickname,
        String authorId,
        String content,
        int rating
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(review.id(), review.authorNickname(), review.userId(), review.content(), review.rating());
    }

    public static List<ReviewResponse> from(List<Review> reviews) {
        return reviews.stream()
                .map(ReviewResponse::from)
                .toList();
    }
}
