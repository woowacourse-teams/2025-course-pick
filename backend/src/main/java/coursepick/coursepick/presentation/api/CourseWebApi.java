package coursepick.coursepick.presentation.api;

import coursepick.coursepick.presentation.dto.CoordinateWebResponse;
import coursepick.coursepick.presentation.dto.CourseWebResponse;
import coursepick.coursepick.presentation.dto.CoursesWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "러닝 코스 (Course)")
public interface CourseWebApi {

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
    CoursesWebResponse findNearbyCourses(
            @Parameter(description = "지도 중심의 위도(-90 ~ 90)", example = "37.5165004", required = true) double mapLatitude,
            @Parameter(description = "지도 중심의 경도(-180 ~ 180)", example = "127.1040109", required = true) double mapLongitude,
            @Parameter(description = "사용자 위치의 위도(-90 ~ 90)", example = "38.5165004") Double userLatitude,
            @Parameter(description = "사용자 위치의 경도(-180 ~ 180)", example = "126.1040109") Double userLongitude,
            @Parameter(description = "좌표 중심으로부터 탐색하고자 하는 범위", example = "1000", required = true) int scope,
            @Parameter(description = "페이지 번호", example = "1") Integer page
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
            @Parameter(description = "코스 ID", example = "689c3143182cecc6353cca7b", required = true) String id,
            @Parameter(description = "사용자 위도(-90 ~ 90)", example = "37.5165004", required = true) double latitude,
            @Parameter(description = "사용자 경도(-180 ~ 180)", example = "127.1040109", required = true) double longitude
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
            @Parameter(description = "코스 ID", example = "689c3143182cecc6353cca7b", required = true) String id,
            @Parameter(description = "사용자 위도(-90 ~ 90)", example = "37.5165004", required = true) double latitude,
            @Parameter(description = "사용자 경도(-180 ~ 180)", example = "127.1040109", required = true) double longitude
    );

    @Operation(summary = "즐겨찾기 코스 조회")
    @ApiResponse(responseCode = "200")
    List<CourseWebResponse> findFavoriteCourses(
            @Parameter(
                    description = "코스 ID 목록",
                    required = true,
                    example = "689c3143182cecc6353cca7b,689c3143182cecc6353cca7c,689c3143182cecc6353cca7d",
                    schema = @Schema(type = "array", implementation = String.class)
            )
            List<String> coursesId
    );
}
