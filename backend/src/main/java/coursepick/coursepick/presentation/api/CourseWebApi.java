package coursepick.coursepick.presentation.api;

import coursepick.coursepick.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

    @Operation(
            summary = "좌표를 도로에 스냅",
            description = "사용자가 입력한 좌표들을 가장 가까운 도로에 정렬하고, 총 거리를 계산하여 반환합니다. OSRM Match API를 사용하여 실제 도로 경로에 맞춰 좌표를 보정합니다."
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

    @Operation(
            summary = "사용자 코스 추가",
            description = "사용자가 직접 새로운 러닝 코스를 생성합니다. 좌표 목록, 코스명, 도로 타입, 난이도를 입력받아 코스를 저장하고, 사용자와의 소유 관계를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "코스 생성 성공",
                    content = @Content(schema = @Schema(implementation = CourseWebResponse.class))
            ),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "위도가 범위 외인 경우",
                            ref = "#/components/examples/INVALID_LATITUDE_RANGE"
                    ),
                    @ExampleObject(
                            name = "경도가 범위 외인 경우",
                            ref = "#/components/examples/INVALID_LONGITUDE_RANGE"
                    ),
                    @ExampleObject(
                            name = "허용되지 않은 도로 타입",
                            ref = "#/components/examples/INVALID_ROAD_TYPE"
                    ),
                    @ExampleObject(
                            name = "허용되지 않은 난이도",
                            ref = "#/components/examples/INVALID_DIFFICULTY"
                    )
            })),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(examples = {
                            @ExampleObject(ref = "#/components/examples/AUTHENTICATION_FAIL")
                    })
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(examples = {
                            @ExampleObject(ref = "#/components/examples/NOT_EXIST_USER")
                    })
            )
    })
    CourseWebResponse create(
            @Parameter(hidden = true) String userId,
            @RequestBody(
                    description = "생성할 코스 정보",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CourseCreateWebRequest.class),
                            examples = @ExampleObject(
                                    name = "코스 생성 요청 예시",
                                    value = """
                                            {
                                              "name": "한강 러닝 코스",
                                              "roadType": "트레일",
                                              "difficulty": "보통",
                                              "coordinates": [
                                                {
                                                  "latitude": 37.5180,
                                                  "longitude": 127.0280
                                                },
                                                {
                                                  "latitude": 37.5175,
                                                  "longitude": 127.0270
                                                },
                                                {
                                                  "latitude": 37.5170,
                                                  "longitude": 127.0265
                                                },
                                                {
                                                  "latitude": 37.5180,
                                                  "longitude": 127.0280
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
            CourseCreateWebRequest courseCreateWebRequest
    );
}
