package coursepick.coursepick.presentation.api;

import coursepick.coursepick.presentation.dto.CoordinateWebResponse;
import coursepick.coursepick.presentation.dto.CourseWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "코스")
public interface CourseWebApi {

    @Operation(hidden = true)
    String syncCourses(String token) throws Exception;

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
            @Parameter(example = "37.5165004", required = true) double mapLatitude,
            @Parameter(example = "127.1040109", required = true) double mapLongitude,
            @Parameter(example = "38.5165004") Double userLatitude,
            @Parameter(example = "126.1040109") Double userLongitude,
            @Parameter(example = "1000", required = true) int scope
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
            @Parameter(example = "689c3143182cecc6353cca7b", required = true) String id,
            @Parameter(example = "37.5165004", required = true) double latitude,
            @Parameter(example = "127.1040109", required = true) double longitude
    );

    @Operation(summary = "특정 코스까지의 길찾기")
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
    List<CoordinateWebResponse> routeToCourse(
            @Parameter(example = "689c3143182cecc6353cca7b", required = true) String id,
            @Parameter(example = "37.5165004", required = true) double latitude,
            @Parameter(example = "127.1040109", required = true) double longitude
    );
}
