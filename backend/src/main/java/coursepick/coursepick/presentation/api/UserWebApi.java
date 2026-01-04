package coursepick.coursepick.presentation.api;

import coursepick.coursepick.presentation.dto.SignWebRequest;
import coursepick.coursepick.presentation.dto.SignWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 (User)")
public interface UserWebApi {

    @Operation(summary = "싸인")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", content = @Content(examples = {
                    @ExampleObject(
                            name = "로그인에 실패한 경우",
                            ref = "#/components/examples/AUTHENTICATION_FAIL"
                    )
            })),
    })
    SignWebResponse sign(
            @RequestBody(
                    required = true,
                    description = "카카오 엑세스토큰"
            )
            SignWebRequest request
    );
}
