package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.notice.Notice;
import io.swagger.v3.oas.annotations.media.Schema;

public record NoticeWebResponse(
        @Schema(description = "공지사항 ID", example = "notice-2024-001")
        String id,
        @Schema(description = "공지사항 제목", example = "코스픽 서비스 점검 안내")
        String title,
        @Schema(description = "공지사항 내용", example = "2024년 1월 1일 서비스 점검이 예정되어 있습니다.")
        String description,
        @Schema(description = "공지사항 이미지 URL", example = "/images/notice.png")
        String imageUrl,
        @Schema(description = "공지사항 클릭 URL", example = "/api/something")
        String targetUrl
) {
    public static NoticeWebResponse create(Notice notice) {
        return new NoticeWebResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getDescription(),
                notice.getImageUrl(),
                notice.getTargetUrl()
        );
    }
}
