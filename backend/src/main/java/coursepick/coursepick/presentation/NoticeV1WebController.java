package coursepick.coursepick.presentation;

import coursepick.coursepick.domain.notice.Notice;
import coursepick.coursepick.presentation.api.NoticeWebApi;
import coursepick.coursepick.presentation.dto.NoticeWebResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class NoticeV1WebController implements NoticeWebApi {

    private final String noticeBaseUrl;

    public NoticeV1WebController(@Value("${notice.base-url}") String noticeBaseUrl) {
        this.noticeBaseUrl = noticeBaseUrl;
    }

    @GetMapping("/notices/{id}")
    public NoticeWebResponse getNotice(@PathVariable("id") String id) {
        return NoticeWebResponse.create(Notice.findById(id), noticeBaseUrl);
    }
}
