package coursepick.coursepick.presentation.api;

import coursepick.coursepick.presentation.dto.NoticesWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "노티스 (Notice)")
public interface NoticeWebApi {

    @Operation(summary = "공지사항 목록 조회", description = "사용자에게 필요한 공지사항 목록을 조회하는 API입니다.")
    @ApiResponse(responseCode = "200")
    NoticesWebResponse getNotices();
}
