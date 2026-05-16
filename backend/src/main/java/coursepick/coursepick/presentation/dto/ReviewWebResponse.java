package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.ReviewResponse;

import java.util.List;

public record ReviewWebResponse(
        String id,
        String content,
        int rating,
        String authorNickname,
        String authorId
) {
    public static ReviewWebResponse from(ReviewResponse reviewResponse) {
        return new ReviewWebResponse(
                reviewResponse.id(),
                reviewResponse.content(),
                reviewResponse.rating(),
                reviewResponse.authorNickname(),
                reviewResponse.authorId()
        );
    }

    public static List<ReviewWebResponse> from(List<ReviewResponse> reviewResponses) {
        return reviewResponses.stream()
                .map(ReviewWebResponse::from)
                .toList();
    }
}
