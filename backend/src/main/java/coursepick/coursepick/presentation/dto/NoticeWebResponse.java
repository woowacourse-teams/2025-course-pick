package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.notice.Notice;

public record NoticeWebResponse(
        String id,
        String title,
        String description,
        String imageUrl,
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
