package coursepick.coursepick.presentation.api;

import coursepick.coursepick.presentation.dto.LoginWebRequest;
import coursepick.coursepick.presentation.dto.LoginWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "인증/인가")
public interface AuthWebApi {

    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", content = @Content(examples = {
                    @ExampleObject(
                            name = "로그인에 실패한 경우",
                            ref = "#/components/examples/LOGIN_FAIL"
                    ),
            })),
    })
    ResponseEntity<LoginWebResponse> login(@RequestBody LoginWebRequest request);
}
