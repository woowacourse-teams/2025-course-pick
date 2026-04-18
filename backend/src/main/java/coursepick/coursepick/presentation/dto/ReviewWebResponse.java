package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.ReviewResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReviewWebResponse(
        @Schema(description = "리뷰 작성자 닉네임", example = "피곤한 하마")
        String authorNickname,
        @Schema(description = "리뷰 내용", example = "노을이 예쁜 코스입니다")
        String content
) {
    public static ReviewWebResponse from(ReviewResponse reviewResponse) {
        return new ReviewWebResponse(reviewResponse.authorNickname(), reviewResponse.content());
    }

    public static List<ReviewWebResponse> from(List<ReviewResponse> reviewResponses) {
        return reviewResponses.stream()
                .map(ReviewWebResponse::from)
                .toList();
    }
}
