package coursepick.coursepick.presentation.api;

import coursepick.coursepick.presentation.dto.NoticeWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "다이얼로그 노티")
public interface NoticeWebApi {

    @Operation(summary = "공지 사항 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(
                            name = "존재하지 않는 공지 사항 조회하는 경우",
                            ref = "#/components/examples/NOT_FOUND_NOTICE"
                    )
            }))
    })
    NoticeWebResponse getNotice(@Parameter(example = "verified_location", required = true, description = "공지 사항 ID") String id);
}
