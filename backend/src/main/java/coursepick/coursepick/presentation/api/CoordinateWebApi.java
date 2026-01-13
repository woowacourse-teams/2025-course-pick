package coursepick.coursepick.presentation.api;

import coursepick.coursepick.presentation.dto.SnapWebRequest;
import coursepick.coursepick.presentation.dto.SnapWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "좌표 (Coordinate)")
public interface CoordinateWebApi {

    @Operation(
            summary = "좌표를 도로에 스냅",
            description = "사용자가 입력한 좌표들을 가장 가까운 도로에 정렬하고, 총 거리를 계산하여 반환합니다. OSRM Match API를 사용하여 실제 도로 경로에 맞춰 좌표를 보정합니다.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "좌표 스냅 성공",
                    content = @Content(schema = @Schema(implementation = SnapWebResponse.class))
            ),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "위도가 범위 외인 경우",
                            ref = "#/components/examples/INVALID_LATITUDE_RANGE"
                    ),
                    @ExampleObject(
                            name = "경도가 범위 외인 경우",
                            ref = "#/components/examples/INVALID_LONGITUDE_RANGE"
                    )
            })),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(examples = {
                            @ExampleObject(ref = "#/components/examples/AUTHENTICATION_FAIL")
                    })
            )
    })
    SnapWebResponse snapCoordinates(
            @RequestBody(
                    description = "도로에 스냅할 좌표 목록",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SnapWebRequest.class),
                            examples = @ExampleObject(
                                    name = "좌표 스냅 요청 예시",
                                    value = """
                                            {
                                              "coordinates": [
                                                {
                                                  "latitude": 37.5180,
                                                  "longitude": 127.0280
                                                },
                                                {
                                                  "latitude": 37.6180,
                                                  "longitude": 127.1280
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
            SnapWebRequest snapWebRequest
    );
}
