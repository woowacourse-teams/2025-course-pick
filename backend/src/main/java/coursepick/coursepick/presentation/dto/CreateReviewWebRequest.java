package coursepick.coursepick.presentation.dto;

public record CreateReviewWebRequest(
        String content,
        int rating
) {
}
