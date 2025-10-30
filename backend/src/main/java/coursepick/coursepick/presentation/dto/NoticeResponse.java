package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.Notice;

public record NoticeResponse(String id, String imageUrl, String title, String description) {
    public static NoticeResponse create(Notice notice) {
        return new NoticeResponse(
                notice.getId(),
                notice.getImageUrl(),
                notice.getTitle(),
                notice.getDescription()
        );
    }
}
