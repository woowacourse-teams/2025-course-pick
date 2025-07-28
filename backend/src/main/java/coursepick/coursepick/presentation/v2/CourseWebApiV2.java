package coursepick.coursepick.presentation.v2;

import coursepick.coursepick.presentation.v2.dto.CoordinateWebResponse;
import coursepick.coursepick.presentation.v2.dto.CourseWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "코스 API V2")
public interface CourseWebApiV2 {

    @Operation(summary = "좌표 근처 1km 내 코스 전체 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
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
    })
    List<CourseWebResponse> findNearbyCourses(
            @Parameter(example = "37.5165004", required = true) double latitude,
            @Parameter(example = "127.1040109", required = true) double longitude
    );

    @Operation(summary = "좌표에서 가장 가까운 코스 위 좌표 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "위도가 범위 외인 경우",
                            ref = "#/components/examples/INVALID_LATITUDE_RANGE"
                    ),
                    @ExampleObject(
                            name = "경도가 범위 외인 경우",
                            ref = "#/components/examples/INVALID_LONGITUDE_RANGE"
                    ),
            })),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(
                            name = "코스가 존재하지 않는 경우",
                            ref = "#/components/examples/NOT_EXIST_COURSE"
                    )
            })),
    })
    CoordinateWebResponse findClosestCoordinate(
            @Parameter(example = "1", required = true) long id,
            @Parameter(example = "37.5165004", required = true) double latitude,
            @Parameter(example = "127.1040109", required = true) double longitude
    );
}
