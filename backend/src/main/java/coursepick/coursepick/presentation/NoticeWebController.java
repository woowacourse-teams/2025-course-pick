package coursepick.coursepick.presentation;

import coursepick.coursepick.domain.Notice;
import coursepick.coursepick.presentation.api.NoticeWebApi;
import coursepick.coursepick.presentation.dto.NoticeWebResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoticeWebController implements NoticeWebApi {

    @GetMapping("/notice/{id}")
    public NoticeWebResponse getNotice(@PathVariable("id") String id) {
        return NoticeWebResponse.create(Notice.findById(id));
    }
}
