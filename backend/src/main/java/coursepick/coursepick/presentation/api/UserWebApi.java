package coursepick.coursepick.presentation.api;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.presentation.dto.SignWebRequest;
import coursepick.coursepick.presentation.dto.SignWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 (User)")
public interface UserWebApi {

    @ApiErrorExceptionsExample({
            ErrorType.AUTHENTICATION_FAIL
    })
    @Operation(summary = "싸인")
    @ApiResponse(responseCode = "200")
    SignWebResponse sign(
            @RequestBody(
                    required = true,
                    description = "카카오 엑세스토큰"
            )
            SignWebRequest request
    );
}
