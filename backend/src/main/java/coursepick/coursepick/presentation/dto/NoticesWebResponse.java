package coursepick.coursepick.presentation.dto;

import java.util.List;

public record NoticesWebResponse(
        List<NoticeWebResponse> notices
) {
}
