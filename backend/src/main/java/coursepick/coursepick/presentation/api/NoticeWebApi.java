package coursepick.coursepick.presentation.api;

import coursepick.coursepick.presentation.dto.NoticeWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "다이얼로그 노티")
public interface NoticeWebApi {

    @Operation(summary = "노티를 위한 조회")
    @ApiResponse(responseCode = "200")
    NoticeWebResponse getNotice(@Parameter(example = "verified_location", required = false) String id);
}
