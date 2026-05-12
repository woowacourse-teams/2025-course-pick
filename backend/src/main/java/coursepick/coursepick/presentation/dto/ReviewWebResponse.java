package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.ReviewResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReviewWebResponse(
        @Schema(description = "리뷰 ID", example = "f47ac10b")
        String id,
        @Schema(description = "리뷰 내용", example = "노을이 예쁜 코스입니다")
        String content,
        @Schema(description = "코스 별점 (1~5)", example = "4")
        int rating,
        @Schema(description = "리뷰 작성자 닉네임", example = "피곤한 하마")
        String authorNickname,
        @Schema(description = "리뷰 작성자 ID", example = "689c3143182cecc6353cca7b")
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
