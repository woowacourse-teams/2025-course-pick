package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.Notice;

public record NoticeWebResponse(String id, String imageUrl, String title, String description) {
    public static NoticeWebResponse create(Notice notice, String baseUrl) {
        String fullImageUrl = baseUrl + "/" + notice.getImageFileName();
        return new NoticeWebResponse(
                notice.getId(),
                fullImageUrl,
                notice.getTitle(),
                notice.getDescription()
        );
    }
}
