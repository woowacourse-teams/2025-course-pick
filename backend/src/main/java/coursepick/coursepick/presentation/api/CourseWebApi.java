package coursepick.coursepick.presentation.api;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "러닝 코스 (Course)")
public interface CourseWebApi {

    @ApiErrorExceptionsExample({
            ErrorType.INVALID_LATITUDE_RANGE,
            ErrorType.INVALID_LONGITUDE_RANGE
    })
    @Operation(summary = "좌표 근처 코스 전체 조회")
    @ApiResponse(responseCode = "200")
    CoursesWebResponse findNearbyCourses(
            @Parameter(description = "지도 중심의 위도(-90 ~ 90)", example = "37.5165004", required = true) double mapLatitude,
            @Parameter(description = "지도 중심의 경도(-180 ~ 180)", example = "127.1040109", required = true) double mapLongitude,
            @Parameter(description = "좌표 중심으로부터 탐색하고자 하는 범위", example = "1000", required = true) int scope,
            @Parameter(description = "사용자 위치의 위도(-90 ~ 90)", example = "38.5165004") Double userLatitude,
            @Parameter(description = "사용자 위치의 경도(-180 ~ 180)", example = "126.1040109") Double userLongitude,
            @Parameter(description = "최소 코스 길이", example = "1000") Integer minLength,
            @Parameter(description = "최대 코스 길이", example = "7000") Integer maxLength,
            @Parameter(description = "페이지 번호", example = "1") Integer page
    );

    @ApiErrorExceptionsExample({
            ErrorType.INVALID_LATITUDE_RANGE,
            ErrorType.INVALID_LONGITUDE_RANGE,
            ErrorType.NOT_EXIST_COURSE
    })
    @Operation(summary = "좌표에서 가장 가까운 코스 위 좌표 조회")
    @ApiResponse(responseCode = "200")
    CoordinateWebResponse findClosestCoordinate(
            @Parameter(description = "코스 ID", example = "689c3143182cecc6353cca7b", required = true) String id,
            @Parameter(description = "사용자 위도(-90 ~ 90)", example = "37.5165004", required = true) double latitude,
            @Parameter(description = "사용자 경도(-180 ~ 180)", example = "127.1040109", required = true) double longitude
    );

    @ApiErrorExceptionsExample({
            ErrorType.INVALID_LATITUDE_RANGE,
            ErrorType.INVALID_LONGITUDE_RANGE,
            ErrorType.NOT_EXIST_COURSE
    })
    @Operation(summary = "특정 코스까지의 길찾기")
    @ApiResponse(responseCode = "200")
    List<CoordinateWebResponse> routeToCourse(
            @Parameter(description = "코스 ID", example = "689c3143182cecc6353cca7b", required = true) String id,
            @Parameter(description = "사용자 위도(-90 ~ 90)", example = "37.5165004", required = true) double latitude,
            @Parameter(description = "사용자 경도(-180 ~ 180)", example = "127.1040109", required = true) double longitude
    );

    @ApiErrorExceptionsExample({
            ErrorType.NOT_EXIST_COURSE
    })
    @Operation(summary = "코스 상세 조회 (리뷰 포함)")
    @ApiResponse(responseCode = "200")
    CourseDetailWebResponse findCourseDetail(
            @Parameter(description = "코스 ID", example = "689c3143182cecc6353cca7b", required = true) String id
    );

    @ApiErrorExceptionsExample({
            ErrorType.INVALID_REVIEW_CONTENT_LENGTH,
            ErrorType.INVALID_REVIEW_RATING,
            ErrorType.NOT_EXIST_COURSE,
            ErrorType.AUTHENTICATION_FAIL
    })
    @Operation(summary = "코스 리뷰 작성", security = {@SecurityRequirement(name = "BearerAuth")})
    @ApiResponse(responseCode = "201")
    void addReview(
            @Parameter(description = "코스 ID", example = "689c3143182cecc6353cca7b", required = true) String id,
            @Parameter(hidden = true) String userId,
            CreateReviewWebRequest request
    );

    @ApiErrorExceptionsExample({
            ErrorType.AUTHENTICATION_FAIL,
            ErrorType.NOT_EXIST_COURSE,
            ErrorType.NOT_EXIST_REVIEW
    })
    @Operation(summary = "코스 리뷰 삭제", security = {@SecurityRequirement(name = "BearerAuth")})
    @ApiResponse(responseCode = "200", description = "리뷰 삭제 완료")
    void deleteReview(
            @Parameter(description = "코스 ID", required = true) String courseId,
            @Parameter(description = "삭제할 리뷰 ID", required = true) String reviewId,
            @Parameter(hidden = true) String userId
    );

    @ApiErrorExceptionsExample({
            ErrorType.ALREADY_REPORTED_REVIEW,
            ErrorType.AUTHENTICATION_FAIL,
            ErrorType.NOT_EXIST_COURSE,
            ErrorType.NOT_EXIST_REVIEW
    })
    @Operation(summary = "리뷰 신고", security = {@SecurityRequirement(name = "BearerAuth")})
    @ApiResponse(responseCode = "200", description = "리뷰 신고 완료")
    void reportCourseReview(
            @Parameter(description = "신고할 리뷰의 코스 ID", required = true) String courseId,
            @Parameter(description = "신고할 리뷰 ID", required = true) String reviewId,
            @Parameter(hidden = true) String userId
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

    @ApiErrorExceptionsExample({
            ErrorType.INVALID_NAME_LENGTH,
            ErrorType.INVALID_COORDINATE_COUNT,
            ErrorType.AUTHENTICATION_FAIL,
            ErrorType.DUPLICATED_COURSE_NAME
    })
    @Operation(summary = "유저 커스텀 코스 등록", security = {@SecurityRequirement(name = "BearerAuth")})
    @ApiResponse(responseCode = "200", description = "코스 등록 성공")
    String addCustomCourses(
            @RequestBody(
                    description = "커스텀 코스 생성 요청 데이터",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "커스텀 코스 요청 예시",
                                    value = "{\n  \"name\": \"매일 뛰는 한강변 코스\",\n  \"coordinates\": [\n    [127.1040109, 37.5165004],\n    [127.1050109, 37.5175004]\n  ]\n}"
                            )
                    )
            )
            CourseCreateWebRequest courseCreateWebRequest,

            @Parameter(hidden = true)
            String userId
    );

    @ApiErrorExceptionsExample({
            ErrorType.INVALID_COORDINATE_COUNT
    })
    @Operation(summary = "코스 생성 시 직전 포인트와 새 포인트 사이의 경로 및 거리 조회 (첫 점인 경우 origin과 destination을 동일하게 전송)")
    @ApiResponse(responseCode = "200")
    DraftRouteWebResponse findDraftRoute(FindDraftRouteWebRequest request);

    @ApiErrorExceptionsExample({
            ErrorType.ALREADY_REPORTED_COURSE,
            ErrorType.AUTHENTICATION_FAIL,
            ErrorType.NOT_EXIST_COURSE
    })
    @Operation(summary = "코스 신고", security = {@SecurityRequirement(name = "BearerAuth")})
    @ApiResponse(responseCode = "200", description = "코스 신고 완료")
    void reportCourse(
            @Parameter(description = "신고할 코스 ID", required = true) String id,
            @Parameter(hidden = true) String userId
    );

    @ApiErrorExceptionsExample({
            ErrorType.AUTHENTICATION_FAIL
    })
    @Operation(summary = "나의 코스 조회(생성순)", security = {@SecurityRequirement(name = "BearerAuth")})
    @ApiResponse(responseCode = "200")
    CoursesWebResponse findCustomCourse(
            @Parameter(description = "사용자 위치의 위도(-90 ~ 90)", example = "38.5165004") Double userLatitude,
            @Parameter(description = "사용자 위치의 경도(-180 ~ 180)", example = "126.1040109") Double userLongitude,
            @Parameter(hidden = true) String userId
    );
}
