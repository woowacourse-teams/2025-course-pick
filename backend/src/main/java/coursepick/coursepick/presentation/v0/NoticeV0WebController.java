package coursepick.coursepick.presentation.v0;

import coursepick.coursepick.domain.Notice;
import coursepick.coursepick.presentation.dto.NoticeWebResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoticeV0WebController implements NoticeV0WebApi {

    private final String noticeBaseUrl;

    public NoticeV0WebController(@Value("${notice.base-url}") String noticeBaseUrl) {
        this.noticeBaseUrl = noticeBaseUrl;
    }

    @GetMapping("/notices/{id}")
    public NoticeWebResponse getNotice(@PathVariable("id") String id) {
        return NoticeWebResponse.create(Notice.findById(id), noticeBaseUrl);
    }
}
