package coursepick.coursepick.presentation;

import coursepick.coursepick.domain.notice.Notice;
import coursepick.coursepick.presentation.api.NoticeWebApi;
import coursepick.coursepick.presentation.dto.NoticeWebResponse;
import coursepick.coursepick.presentation.dto.NoticesWebResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/v1")
public class NoticeV1WebController implements NoticeWebApi {

    @GetMapping("/notices")
    public NoticesWebResponse getNotices() {
        return new NoticesWebResponse(Arrays.stream(Notice.values())
                .map(NoticeWebResponse::create)
                .toList());
    }
}
