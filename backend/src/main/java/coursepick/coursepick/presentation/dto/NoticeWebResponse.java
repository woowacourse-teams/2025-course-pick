package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.Notice;

public record NoticeWebResponse(String id, String imageUrl, String title, String description) {
    public static NoticeWebResponse create(Notice notice) {
        return new NoticeWebResponse(
                notice.getId(),
                notice.getImageUrl(),
                notice.getTitle(),
                notice.getDescription()
        );
    }
}
