package coursepick.coursepick.docs;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import coursepick.coursepick.application.dto.CourseDetailResponse;
import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.application.dto.CoursesResponse;
import coursepick.coursepick.application.dto.ReviewResponse;
import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.presentation.CourseV1WebController;
import coursepick.coursepick.presentation.dto.CoordinateWebRequest;
import coursepick.coursepick.presentation.dto.CourseCreateWebRequest;
import coursepick.coursepick.presentation.dto.CreateReviewWebRequest;
import coursepick.coursepick.presentation.dto.FindDraftRouteWebRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultHandler;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class CourseApiDocsTest extends AbstractApiDocsSupport {

    private static final String TAG = "러닝 코스 (Course)";
    private static final String 다음_페이지_존재_여부 = "다음 페이지 존재 여부";
    private static final String 리뷰_ID = "리뷰 ID";
    private static final String 사용자_위치로부터의_거리 = "사용자 위치로부터의 거리 (미터)";
    private static final String 코스_ID = "코스 ID";
    private static final String 위도 = "위도";
    private static final String 경도 = "경도";
    private static final String 사용자_위도 = "사용자 위치 위도(-90 ~ 90)";
    private static final String 사용자_경도 = "사용자 위치 경도(-180 ~ 180)";
    private static final String 코스_이름 = "코스 이름";
    private static final String 코스_전체_길이 = "코스 전체 길이 (미터)";
    private static final String 코스_등록_주체_ID = "코스 등록 주체 ID";
    private static final String 리뷰_내용 = "리뷰 내용";
    private static final String 리뷰_별점 = "리뷰 별점";

    @Override
    Object initController() {
        return new CourseV1WebController(super.courseApplicationService);
    }

    @Nested
    @DisplayName("정상 응답 API")
    class SuccessCases {

        @Test
        void 좌표_근처_코스_전체_조회_API() throws Exception {
            var courseResponse = new CourseResponse(
                    "689c3143182cecc6353cca7b",
                    "석촌호수",
                    new Meter(200.123),
                    new Meter(2146.123),
                    List.of(
                            new Coordinate(37.514167, 127.103611),
                            new Coordinate(37.515167, 127.104611)),
                    "adminId");

            var coursesResponse = new CoursesResponse(List.of(courseResponse), true);
            given(courseApplicationService.findNearbyCourses(any(CourseFindCondition.class), anyDouble(),
                    anyDouble()))
                    .willReturn(coursesResponse);

            mockMvc.perform(get("/v1/courses")
                            .param("mapLat", "37.5165004")
                            .param("mapLng", "127.1040109")
                            .param("scope", "1000")
                            .param("userLat", "38.5165004")
                            .param("userLng", "126.1040109")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper.document("course-find-nearby",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("좌표 근처 코스 조회")
                                    .description("지도 중심 좌표와 범위를 기반으로 주변 코스를 조회합니다.")
                                    .queryParameters(
                                            parameterWithName("mapLat").description(
                                                            "지도 중심의 위도(-90 ~ 90)")
                                                    .attributes(key("example")
                                                            .value("37.5165")),
                                            parameterWithName("mapLng").description(
                                                            "지도 중심의 경도(-180 ~ 180)")
                                                    .attributes(key("example")
                                                            .value("127.1040")),
                                            parameterWithName("scope").description(
                                                            "좌표 중심으로부터 탐색하고자 하는 범위 (미터)")
                                                    .attributes(key("example")
                                                            .value("1000")),
                                            parameterWithName("userLat")
                                                    .description(사용자_위도)
                                                    .optional()
                                                    .attributes(key("example")
                                                            .value("37.5165")),
                                            parameterWithName("userLng")
                                                    .description(사용자_경도)
                                                    .optional()
                                                    .attributes(key("example")
                                                            .value("127.1040")),
                                            parameterWithName("minLength")
                                                    .description("최소 코스 길이")
                                                    .optional()
                                                    .attributes(key("example")
                                                            .value("0")),
                                            parameterWithName("maxLength")
                                                    .description("최대 코스 길이")
                                                    .optional()
                                                    .attributes(key("example")
                                                            .value("10000")),
                                            parameterWithName("page")
                                                    .description("페이지 번호")
                                                    .optional()
                                                    .attributes(key("example")
                                                            .value("0")))
                                    .responseFields(
                                            fieldWithPath("courses[].id")
                                                    .description(코스_ID),
                                            fieldWithPath("courses[].name")
                                                    .description(코스_이름),
                                            fieldWithPath("courses[].distance")
                                                    .description(사용자_위치로부터의_거리)
                                                    .optional(),
                                            fieldWithPath("courses[].length")
                                                    .description(코스_전체_길이),
                                            fieldWithPath("courses[].coordinates[].latitude")
                                                    .description(위도),
                                            fieldWithPath("courses[].coordinates[].longitude")
                                                    .description(경도),
                                            fieldWithPath("courses[].creatorId")
                                                    .description(코스_등록_주체_ID),
                                            fieldWithPath("hasNext").description(
                                                    다음_페이지_존재_여부))
                                    .build())));
        }

        @Test
        void 좌표에서_가장_가까운_코스_위_좌표_조회_API() throws Exception {
            given(courseApplicationService.findClosestCoordinate(anyString(), anyDouble(), anyDouble()))
                    .willReturn(new Coordinate(37.514167, 127.103611));

            mockMvc.perform(get("/v1/courses/{id}/closest-coordinate", "69d8c0b55561463adc32f259")
                            .param("lat", "37.5165004")
                            .param("lng", "127.1040109")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper.document("course-closest-coordinate",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("좌표에서 가장 가까운 코스 위 좌표 조회")
                                    .pathParameters(
                                            parameterWithName("id")
                                                    .description(코스_ID)
                                                    .attributes(key("example")
                                                            .value("69d8c0b55561463adc32f259")))
                                    .queryParameters(
                                            parameterWithName("lat")
                                                    .description(사용자_위도)
                                                    .attributes(key("example")
                                                            .value("37.5165")),
                                            parameterWithName("lng")
                                                    .description(사용자_경도)
                                                    .attributes(key("example")
                                                            .value("127.1040")))
                                    .responseFields(
                                            fieldWithPath("latitude")
                                                    .description(위도),
                                            fieldWithPath("longitude")
                                                    .description(경도))
                                    .build())));
        }

        @Test
        void 특정_코스까지의_길찾기_API() throws Exception {
            given(courseApplicationService.routesToCourse(anyString(), anyDouble(), anyDouble()))
                    .willReturn(List.of(
                            new Coordinate(37.514167, 127.103611),
                            new Coordinate(37.515167, 127.104611)));

            mockMvc.perform(get("/v1/courses/{id}/route", "69d8c0b55561463adc32f259")
                            .param("startLat", "37.5165004")
                            .param("startLng", "127.1040109")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper.document("course-route",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("특정 코스까지의 길찾기")
                                    .pathParameters(
                                            parameterWithName("id")
                                                    .description(코스_ID)
                                                    .attributes(key("example")
                                                            .value("69d8c0b55561463adc32f259")))
                                    .queryParameters(
                                            parameterWithName("startLat")
                                                    .description(사용자_위도)
                                                    .attributes(key("example")
                                                            .value("37.5165")),
                                            parameterWithName("startLng")
                                                    .description(사용자_경도)
                                                    .attributes(key("example")
                                                            .value("127.1040")))
                                    .responseFields(
                                            fieldWithPath("[].latitude")
                                                    .description(위도),
                                            fieldWithPath("[].longitude")
                                                    .description(경도))
                                    .build())));
        }

        @Test
        void 코스_상세_조회_API() throws Exception {
            var detailResponse = new CourseDetailResponse(
                    "69d8c0b55561463adc32f259",
                    "서울둘레8코스",
                    new Meter(2146.123),
                    List.of(new Coordinate(37.514167, 127.103611)),
                    1,
                    5.0,
                    List.of(new ReviewResponse("69d8c0b54561463adc32f259", "착한 강아지",
                            "69d8c0b12361463adc32f259", "노을이 예뻐요.", 5)),
                    List.of(CourseTag.NIGHT_VIEW, CourseTag.SCENIC)
            );
            given(courseApplicationService.findCourseDetail(anyString()))
                    .willReturn(detailResponse);

            mockMvc.perform(get("/v1/courses/{id}", "69d8c0b55561463adc32f259")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper.document("course-detail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("코스 상세 조회 (리뷰 포함)")
                                    .pathParameters(
                                            parameterWithName("id")
                                                    .description(코스_ID)
                                                    .attributes(key("example")
                                                            .value("69d8c0b55561463adc32f259")))
                                    .responseFields(
                                            fieldWithPath("id").description(코스_ID),
                                            fieldWithPath("name")
                                                    .description(코스_이름),
                                            fieldWithPath("length")
                                                    .description(코스_전체_길이),
                                            fieldWithPath("coordinates[].latitude")
                                                    .description(위도),
                                            fieldWithPath("coordinates[].longitude")
                                                    .description(경도),
                                            fieldWithPath("reviewOverview.reviewCount")
                                                    .description("리뷰 개수"),
                                            fieldWithPath("reviewOverview.averageRating")
                                                    .description("리뷰 평균 별점"),
                                            fieldWithPath("reviews[].id")
                                                    .description(리뷰_ID),
                                            fieldWithPath("reviews[].rating")
                                                    .description(리뷰_별점),
                                            fieldWithPath("reviews[].authorId")
                                                    .description("리뷰 작성자 ID"),
                                            fieldWithPath("reviews[].authorNickname")
                                                    .description("리뷰 작성자 닉네임"),
                                            fieldWithPath("reviews[].content")
                                                    .description(리뷰_내용),
                                            fieldWithPath("tags[].name").description("태그 이름 (enum 값)"),
                                            fieldWithPath("tags[].label").description("태그 라벨 (UI 표시용)")
                                    )
                                    .build())));
        }

        @Test
        void 즐겨찾기_코스_조회_API() throws Exception {
            var courseResponse = new CourseResponse(
                    "689c3143182cecc6353cca7b",
                    "석촌호수",
                    null,
                    new Meter(2146.123),
                    List.of(new Coordinate(37.514167, 127.103611)),
                    "adminId");
            given(courseApplicationService.findFavoriteCourses(any()))
                    .willReturn(List.of(courseResponse));

            mockMvc.perform(get("/v1/courses/favorites")
                            .param("courseIds", "689c3143182cecc6353cca7b", "689c3143182cecc6353cca7c")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper.document("course-favorites",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("즐겨찾기 코스 조회")
                                    .queryParameters(
                                            parameterWithName("courseIds")
                                                    .description("코스 ID 목록 (쉼표 구분)")
                                                    .attributes(key("example")
                                                            .value("689c3143182cecc6353cca7b,689c3143182cecc6353cca7c")))
                                    .responseFields(
                                            fieldWithPath("[].id")
                                                    .description(코스_ID),
                                            fieldWithPath("[].name")
                                                    .description(코스_이름),
                                            fieldWithPath("[].distance")
                                                    .description(사용자_위치로부터의_거리)
                                                    .optional(),
                                            fieldWithPath("[].length")
                                                    .description(코스_전체_길이),
                                            fieldWithPath("[].coordinates[].latitude")
                                                    .description(위도),
                                            fieldWithPath("[].coordinates[].longitude")
                                                    .description(경도),
                                            fieldWithPath("[].creatorId")
                                                    .description(코스_등록_주체_ID))
                                    .build())));
        }

        @Test
        void 코스_생성_시_직전_포인트와_새_포인트_사이의_경로_조회_API() throws Exception {
            var draftSegment = DraftSegment.of(List.of(
                    new Coordinate(37.514167, 127.103611),
                    new Coordinate(37.515167, 127.104611)));
            given(courseApplicationService.findDraftRoute(any()))
                    .willReturn(draftSegment);

            var request = new FindDraftRouteWebRequest(
                    new CoordinateWebRequest(37.514167, 127.103611),
                    new CoordinateWebRequest(37.515167, 127.104611)
            );
            var requestBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/v1/courses/draft/route")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper.document("course-draft-route",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("코스 생성 시 직전 포인트와 새 포인트 사이의 경로 및 거리 조회")
                                    .requestFields(
                                            fieldWithPath("origin.latitude")
                                                    .description("직전 포인트 위도")
                                                    .attributes(key("example")
                                                            .value("37.514167")),
                                            fieldWithPath("origin.longitude")
                                                    .description("직전 포인트 경도")
                                                    .attributes(key("example")
                                                            .value("127.103611")),
                                            fieldWithPath("destination.latitude")
                                                    .description("새 포인트 위도")
                                                    .attributes(key("example")
                                                            .value("37.515167")),
                                            fieldWithPath("destination.longitude")
                                                    .description("새 포인트 경도")
                                                    .attributes(key("example")
                                                            .value("127.104611")))
                                    .responseFields(
                                            fieldWithPath("coordinates[].latitude")
                                                    .description(위도),
                                            fieldWithPath("coordinates[].longitude")
                                                    .description(경도),
                                            fieldWithPath("length")
                                                    .description(코스_전체_길이))
                                    .build())));
        }

        @Test
        void 코스_리뷰_작성_API() throws Exception {
            doNothing().when(courseApplicationService).addReview(anyString(), anyString(), anyString(), anyInt());

            var request = new CreateReviewWebRequest("정말 멋진 러닝 코스입니다!", 5);
            var requestBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/v1/courses/{id}/reviews", "689c3143182cecc6353cca7b")
                            .header("Authorization", "Bearer " + "testToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper.document("course-add-review",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("코스 리뷰 작성")
                                    .description("특정 코스에 리뷰를 작성합니다. (로그인 필요)")
                                    .pathParameters(
                                            parameterWithName("id")
                                                    .description(코스_ID)
                                                    .attributes(key("example")
                                                            .value("689c3143182cecc6353cca7b")))
                                    .requestFields(
                                            fieldWithPath("content")
                                                    .description(리뷰_내용)
                                                    .attributes(key("example")
                                                            .value("정말 멋진 러닝 코스입니다!")),
                                            fieldWithPath("rating")
                                                    .description(리뷰_별점)
                                                    .attributes(key("example")
                                                            .value(5)))
                                    .build())));
        }



        @Test
        void 커스텀_코스_생성_API() throws Exception {
            doNothing().when(courseApplicationService).addCustomCourse(anyString(), anyList(), anyString());

            var request = new CourseCreateWebRequest(
                    "나만의 한강 러닝 코스",
                    List.of(
                            new CoordinateWebRequest(37.514167, 127.103611),
                            new CoordinateWebRequest(37.515167, 127.104611)
                    )
            );
            var requestBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/v1/courses")
                            .header("Authorization", "Bearer " + "test.jwt.token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper.document("course-add-custom",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("커스텀 코스 생성")
                                    .description("사용자가 직접 커스텀 코스를 생성합니다. (로그인 필요)")
                                    .requestFields(
                                            fieldWithPath("name")
                                                    .description("코스 이름")
                                                    .attributes(key("example")
                                                            .value("나만의 한강 러닝 코스")),
                                            fieldWithPath("coordinates[].latitude")
                                                    .description(위도)
                                                    .attributes(key("example")
                                                            .value("37.514167")),
                                            fieldWithPath("coordinates[].longitude")
                                                    .description(경도)
                                                    .attributes(key("example")
                                                            .value("127.103611")))
                                    .build())));
        }

        @Test
        void 코스_신고_API() throws Exception {
            doNothing().when(courseApplicationService).reportCourse(anyString(), anyString());

            mockMvc.perform(post("/v1/courses/{id}/report", "689c3143182cecc6353cca7b")
                            .header("Authorization", "Bearer " + "test.jwt.token")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper.document("course-report",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("코스 신고")
                                    .description("부적절한 코스를 신고합니다. (로그인 필요)")
                                    .pathParameters(
                                            parameterWithName("id")
                                                    .description(코스_ID)
                                                    .attributes(key("example")
                                                            .value("689c3143182cecc6353cca7b")))
                                    .build())));
        }

        @Test
        void 내_커스텀_코스_조회_API() throws Exception {
            var courseResponse = new CourseResponse(
                    "custom_id_123",
                    "나만의 한강 코스",
                    new Meter(100.0),
                    new Meter(5000.0),
                    List.of(new Coordinate(37.514, 127.103)),
                    "userId");
            var coursesResponse = new CoursesResponse(List.of(courseResponse), false);
            given(courseApplicationService.findCustomCourses(any(), nullable(Double.class), nullable(Double.class)))
                    .willReturn(coursesResponse);

            mockMvc.perform(get("/v1/courses/custom")
                            .header("Authorization", "Bearer " + "testToken")
                            .param("userLat", "37.516")
                            .param("userLng", "127.104")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper.document("course-find-custom",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("내 커스텀 코스 조회")
                                    .description("내가 생성한 커스텀 코스 목록을 조회합니다. (로그인 필요)")
                                    .queryParameters(
                                            parameterWithName("userLat")
                                                    .description(사용자_위도)
                                                    .optional()
                                                    .attributes(key("example")
                                                            .value("37.516")),
                                            parameterWithName("userLng")
                                                    .description(사용자_경도)
                                                    .optional()
                                                    .attributes(key("example")
                                                            .value("127.104")))
                                    .responseFields(
                                            fieldWithPath("courses[].id")
                                                    .description(코스_ID),
                                            fieldWithPath("courses[].name")
                                                    .description(코스_이름),
                                            fieldWithPath("courses[].distance")
                                                    .description(사용자_위치로부터의_거리)
                                                    .optional(),
                                            fieldWithPath("courses[].length")
                                                    .description(코스_전체_길이),
                                            fieldWithPath("courses[].coordinates[].latitude")
                                                    .description(위도),
                                            fieldWithPath("courses[].coordinates[].longitude")
                                                    .description(경도),
                                            fieldWithPath("courses[].creatorId")
                                                    .description(코스_등록_주체_ID),
                                            fieldWithPath("hasNext").description(
                                                    다음_페이지_존재_여부))
                                    .build())));
        }

        @Test
        void 코스_리뷰_삭제_API() throws Exception {
            doNothing().when(courseApplicationService).deleteReview(anyString(), anyString(), anyString());

            mockMvc.perform(delete("/v1/courses/{courseId}/reviews/{reviewId}", "course-id", "review-id")
                            .header("Authorization", "Bearer " + "test.jwt.token")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper.document("course-delete-review",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("코스 리뷰 삭제")
                                    .description("작성한 리뷰를 삭제합니다. (로그인 필요)")
                                    .pathParameters(
                                            parameterWithName("courseId")
                                                    .description(코스_ID)
                                                    .attributes(key("example").value("course-id")),
                                            parameterWithName("reviewId")
                                                    .description(리뷰_ID)
                                                    .attributes(key("example").value("review-id")))
                                    .build())));
        }

        @Test
        void 코스_리뷰_신고_API() throws Exception {
            doNothing().when(courseApplicationService).reportReview(anyString(), anyString(), anyString());

            mockMvc.perform(post("/v1/courses/{courseId}/reviews/{reviewId}/report", "course-id", "review-id")
                            .header("Authorization", "Bearer " + "test.jwt.token")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcRestDocumentationWrapper.document("course-report-review",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("코스 리뷰 신고")
                                    .description("부적절한 리뷰를 신고합니다. (로그인 필요)")
                                    .pathParameters(
                                            parameterWithName("courseId")
                                                    .description("코스 ID")
                                                    .attributes(key("example").value("course-id")),
                                            parameterWithName("reviewId")
                                                    .description(리뷰_ID)
                                                    .attributes(key("example").value("review-id")))
                                    .build())));
        }
    }

    @Nested
    @DisplayName("예외 및 에러 응답 API")
    class ExceptionCases {

        @Test
        void 코스_리뷰_작성_API_400_에러_별점() throws Exception {
            doThrow(ErrorType.INVALID_REVIEW_RATING.create(6))
                    .when(courseApplicationService).addReview(anyString(), any(), anyString(), anyInt());

            var request = new CreateReviewWebRequest("정말 멋진 러닝 코스입니다!", 6);
            var requestBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/v1/courses/{id}/reviews", "689c3143182cecc6353cca7b")
                            .header("Authorization", "Bearer " + "test.jwt.token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(documentBadRequest("course-add-review-400-rating"));
        }

        @Test
        void 코스_리뷰_작성_API_400_에러_글자수() throws Exception {
            doThrow(ErrorType.INVALID_REVIEW_CONTENT_LENGTH.create(0))
                    .when(courseApplicationService).addReview(anyString(), any(), anyString(), anyInt());

            var request = new CreateReviewWebRequest("", 5);
            var requestBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/v1/courses/{id}/reviews", "689c3143182cecc6353cca7b")
                            .header("Authorization", "Bearer " + "test.jwt.token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(documentBadRequest("course-add-review-400-length"));
        }

        @Test
        void 코스_리뷰_작성_API_400_에러_중복() throws Exception {
            doThrow(ErrorType.ALREADY_REVIEWED_COURSE.create("689c3143182cecc6353cca7b", "user-id"))
                    .when(courseApplicationService).addReview(anyString(), any(), anyString(), anyInt());

            var request = new CreateReviewWebRequest("정말 멋진 러닝 코스입니다!", 5);
            var requestBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/v1/courses/{id}/reviews", "689c3143182cecc6353cca7b")
                            .header("Authorization", "Bearer " + "test.jwt.token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(documentBadRequest("course-add-review-400-duplicate"));
        }

        @Test
        void 코스_리뷰_작성_API_401_에러() throws Exception {
            doThrow(ErrorType.AUTHENTICATION_FAIL
                    .create())
                    .when(courseApplicationService).addReview(anyString(), any(), anyString(), anyInt());

            var request = new CreateReviewWebRequest("정말 멋진 러닝 코스입니다!", 5);
            var requestBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/v1/courses/{id}/reviews", "689c3143182cecc6353cca7b")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andDo(documentUnauthorizedWithId("course-add-review-401", "코스 리뷰 작성"));
        }

        @Test
        void 커스텀_코스_생성_API_401_에러() throws Exception {
            doThrow(ErrorType.AUTHENTICATION_FAIL
                    .create())
                    .when(courseApplicationService).addCustomCourse(any(), anyList(), any());

            var request = new CourseCreateWebRequest(
                    "나만의 한강 러닝 코스",
                    List.of(
                            new CoordinateWebRequest(37.514167, 127.103611),
                            new CoordinateWebRequest(37.515167, 127.104611)
                    )
            );
            var requestBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/v1/courses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andDo(documentUnauthorized("course-add-custom-401", "커스텀 코스 생성"));
        }

        @Test
        void 코스_신고_API_401_에러() throws Exception {
            doThrow(ErrorType.AUTHENTICATION_FAIL
                    .create())
                    .when(courseApplicationService).reportCourse(anyString(), any());

            mockMvc.perform(post("/v1/courses/{id}/report", "689c3143182cecc6353cca7b")
                            // Authorization 헤더를 누락하여 401 유도
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andDo(documentUnauthorizedWithId("course-report-401", "코스 신고"));
        }

        @Test
        void 내_커스텀_코스_조회_API_401_에러() throws Exception {
            doThrow(ErrorType.AUTHENTICATION_FAIL
                    .create())
                    .when(courseApplicationService).findCustomCourses(any(), any(), any());

            mockMvc.perform(get("/v1/courses/custom")
                            .param("userLat", "37.516")
                            .param("userLng", "127.104")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andDo(documentUnauthorized("course-find-custom-401", "내 커스텀 코스 조회"));
        }

        @Test
        void 코스_상세_조회_API_404_에러() throws Exception {
            doThrow(ErrorType.NOT_EXIST_COURSE.create("invalid-id"))
                    .when(courseApplicationService).findCourseDetail(anyString());

            mockMvc.perform(get("/v1/courses/{id}", "invalid-id")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(documentNotFound("course-detail-404", "코스 상세 조회"));
        }

        @Test
        void 코스_가장_가까운_좌표_조회_API_404_에러() throws Exception {
            given(courseApplicationService.findClosestCoordinate(anyString(), anyDouble(), anyDouble()))
                    .willThrow(ErrorType.NOT_EXIST_COURSE.create("invalid-id"));

            mockMvc.perform(get("/v1/courses/{id}/closest-coordinate", "invalid-id")
                            .param("lat", "37.5165")
                            .param("lng", "127.1040")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(documentNotFound("course-closest-coordinate-404", "좌표에서 가장 가까운 코스 위 좌표 조회"));
        }

        @Test
        void 특정_코스까지의_길찾기_API_404_에러() throws Exception {
            given(courseApplicationService.routesToCourse(anyString(), anyDouble(), anyDouble()))
                    .willThrow(ErrorType.NOT_EXIST_COURSE.create("invalid-id"));

            mockMvc.perform(get("/v1/courses/{id}/route", "invalid-id")
                            .param("startLat", "37.5165")
                            .param("startLng", "127.1040")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(documentNotFound("course-route-404", "특정 코스까지의 길찾기"));
        }

        @Test
        void 코스_리뷰_작성_API_404_에러() throws Exception {
            doThrow(ErrorType.NOT_EXIST_COURSE.create("invalid-id"))
                    .when(courseApplicationService).addReview(anyString(), any(), anyString(), anyInt());

            var request = new CreateReviewWebRequest("정말 멋진 러닝 코스입니다!", 5);
            var requestBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/v1/courses/{id}/reviews", "invalid-id")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(documentNotFound("course-add-review-404", "코스 리뷰 작성"));
        }

        @Test
        void 코스_신고_API_404_에러() throws Exception {
            doThrow(ErrorType.NOT_EXIST_COURSE.create("invalid-id"))
                    .when(courseApplicationService).reportCourse(anyString(), any());

            mockMvc.perform(post("/v1/courses/{id}/report", "invalid-id")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(documentNotFound("course-report-404", "코스 신고"));
        }

        @Test
        void 커스텀_코스_생성_API_409_에러() throws Exception {
            doThrow(ErrorType.DUPLICATED_COURSE_NAME.create("나만의 한강 러닝 코스"))
                    .when(courseApplicationService).addCustomCourse(any(), anyList(), any());

            var request = new CourseCreateWebRequest(
                    "나만의 한강 러닝 코스",
                    List.of(
                            new CoordinateWebRequest(37.514167, 127.103611),
                            new CoordinateWebRequest(37.515167, 127.104611)
                    )
            );
            var requestBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/v1/courses")
                            .header("Authorization", "Bearer " + "test.jwt.token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andDo(documentConflict("course-add-custom-409", "커스텀 코스 생성"));
        }

        @Test
        void 코스_리뷰_삭제_API_401_에러() throws Exception {
            doThrow(ErrorType.AUTHENTICATION_FAIL.create())
                    .when(courseApplicationService).deleteReview(anyString(), anyString(), anyString());

            mockMvc.perform(delete("/v1/courses/{courseId}/reviews/{reviewId}", "course-id", "review-id")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andDo(documentUnauthorizedForReview("course-delete-review-401", "코스 리뷰 삭제"));
        }

        @Test
        void 코스_리뷰_신고_API_401_에러() throws Exception {
            doThrow(ErrorType.AUTHENTICATION_FAIL.create())
                    .when(courseApplicationService).reportReview(anyString(), anyString(), anyString());

            mockMvc.perform(post("/v1/courses/{courseId}/reviews/{reviewId}/report", "course-id", "review-id")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andDo(documentUnauthorizedForReview("course-report-review-401", "코스 리뷰 신고"));
        }

        @Test
        void 코스_리뷰_삭제_API_404_에러() throws Exception {
            doThrow(ErrorType.NOT_EXIST_REVIEW.create("review-id"))
                    .when(courseApplicationService).deleteReview(anyString(), anyString(), anyString());

            mockMvc.perform(delete("/v1/courses/{courseId}/reviews/{reviewId}", "course-id", "review-id")
                            .header("Authorization", "Bearer " + "test.jwt.token")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(documentNotFoundForReview("course-delete-review-404", "코스 리뷰 삭제"));
        }

        @Test
        void 코스_리뷰_신고_API_404_에러() throws Exception {
            doThrow(ErrorType.NOT_EXIST_REVIEW.create("review-id"))
                    .when(courseApplicationService).reportReview(anyString(), anyString(), anyString());

            mockMvc.perform(post("/v1/courses/{courseId}/reviews/{reviewId}/report", "course-id", "review-id")
                            .header("Authorization", "Bearer " + "test.jwt.token")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(documentNotFoundForReview("course-report-review-404", "코스 리뷰 신고"));
        }

        private FieldDescriptor[] errorResponseFields() {
            return new FieldDescriptor[]{
                    fieldWithPath("message").description("에러 상세 메시지"),
                    fieldWithPath("timestamp").description("에러 발생 시각")
            };
        }

        private ResultHandler documentUnauthorized(String documentId, String summary) {
            return MockMvcRestDocumentationWrapper.document(documentId,
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                            .tag(TAG)
                            .summary(summary)
                            .description("로그인 토큰이 없거나 유효하지 않은 경우의 에러 응답입니다.")
                            .responseFields(errorResponseFields())
                            .build())
            );
        }

        private ResultHandler documentUnauthorizedWithId(String documentId, String summary) {
            return MockMvcRestDocumentationWrapper.document(documentId,
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                            .tag(TAG)
                            .summary(summary)
                            .description("로그인 토큰이 없거나 유효하지 않은 경우의 에러 응답입니다.")
                            .pathParameters(
                                    parameterWithName("id")
                                            .description(코스_ID)
                                            .attributes(key("example").value("689c3143182cecc6353cca7b")))
                            .responseFields(errorResponseFields())
                            .build())
            );
        }

        private ResultHandler documentNotFound(String documentId, String summary) {
            return MockMvcRestDocumentationWrapper.document(documentId,
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                            .tag(TAG)
                            .summary(summary)
                            .description("존재하지 않는 코스 ID를 요청할 경우의 에러 응답입니다.")
                            .pathParameters(
                                    parameterWithName("id")
                                            .description(코스_ID)
                                            .attributes(key("example")
                                                    .value("invalid-id")))
                            .responseFields(errorResponseFields())
                            .build()));
        }

        private ResultHandler documentConflict(String documentId, String summary) {
            return MockMvcRestDocumentationWrapper.document(documentId,
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                            .tag(TAG)
                            .summary(summary)
                            .description("중복된 데이터로 인해 리소스 생성에 실패한 경우의 에러 응답입니다.")
                            .responseFields(errorResponseFields())
                            .build())
            );
        }

        private ResultHandler documentNotFoundForReview(String documentId, String summary) {
            return MockMvcRestDocumentationWrapper.document(documentId,
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                            .tag(TAG)
                            .summary(summary)
                            .description("존재하지 않는 리소스를 요청할 경우의 에러 응답입니다.")
                            .pathParameters(
                                    parameterWithName("courseId").description(코스_ID).attributes(key("example").value("course-id")),
                                    parameterWithName("reviewId").description(리뷰_ID).attributes(key("example").value("review-id")))
                            .responseFields(errorResponseFields())
                            .build())
            );
        }

        private ResultHandler documentUnauthorizedForReview(String documentId, String summary) {
            return MockMvcRestDocumentationWrapper.document(documentId,
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                            .tag(TAG)
                            .summary(summary)
                            .description("로그인 토큰이 없거나 유효하지 않은 경우의 에러 응답입니다.")
                            .pathParameters(
                                    parameterWithName("courseId").description(코스_ID).attributes(key("example").value("course-id")),
                                    parameterWithName("reviewId").description(리뷰_ID).attributes(key("example").value("review-id")))
                            .responseFields(errorResponseFields())
                            .build())
            );
        }

        private ResultHandler documentBadRequest(String documentId) {
            return MockMvcRestDocumentationWrapper.document(documentId,
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                            .tag(TAG)
                            .responseFields(errorResponseFields())
                            .build()));
        }
    }

}
