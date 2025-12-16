package coursepick.coursepick.presentation.v1;

import coursepick.coursepick.presentation.dto.CoursesWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "러닝 코스 (Course) v1")
public interface CourseV1WebApi {

    @Operation(summary = "근처 코스 조회")
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
    CoursesWebResponse findNearbyCourses(
            @Parameter(description = "지도 중심의 위도(-90 ~ 90)", example = "37.5165004", required = true) double mapLatitude,
            @Parameter(description = "지도 중심의 경도(-180 ~ 180)", example = "127.1040109", required = true) double mapLongitude,
            @Parameter(description = "사용자 위치의 위도(-90 ~ 90)", example = "38.5165004") Double userLatitude,
            @Parameter(description = "사용자 위치의 경도(-180 ~ 180)", example = "126.1040109") Double userLongitude,
            @Parameter(description = "좌표 중심으로부터 탐색하고자 하는 범위", example = "1000", required = true) int scope,
            @Parameter(description = "페이지 번호", example = "1") Integer page
    );
}
