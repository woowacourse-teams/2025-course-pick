package coursepick.coursepick.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.CoursesResponse;
import coursepick.coursepick.domain.course.CourseFindCondition;
import coursepick.coursepick.domain.course.DraftSegment;
import coursepick.coursepick.presentation.dto.CoursesWebResponse;
import coursepick.coursepick.presentation.dto.DraftRouteWebResponse;
import coursepick.coursepick.presentation.dto.FindDraftRouteWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v2")
@RequiredArgsConstructor
public class CourseV2WebController {

    private final CourseApplicationService courseApplicationService;

    @GetMapping("/courses")
    public CoursesWebResponse findNearbyCourses(
            @RequestParam("mapLat") double mapLatitude,
            @RequestParam("mapLng") double mapLongitude,
            @RequestParam("scope") int scope,
            @RequestParam(value = "userLat", required = false) Double userLatitude,
            @RequestParam(value = "userLng", required = false) Double userLongitude,
            @RequestParam(value = "minLength", required = false) Integer minLength,
            @RequestParam(value = "maxLength", required = false) Integer maxLength,
            @RequestParam(value = "page", required = false) Integer page
    ) {
        CourseFindCondition condition = new CourseFindCondition(mapLatitude, mapLongitude, scope, minLength, maxLength, page);
        CoursesResponse response = courseApplicationService.findNearbyCourses(condition, userLatitude, userLongitude);
        return CoursesWebResponse.from(response);
    }

    @Operation(summary = "코스 생성 시 직전 포인트와 새 포인트 사이의 경로 및 거리 조회 (첫 점인 경우 origin과 destination을 동일하게 전송)")
    @ApiResponse(responseCode = "200")
    @PostMapping("/courses/draft/route")
    public DraftRouteWebResponse findDraftRoute(@Valid @RequestBody FindDraftRouteWebRequest request) {
        DraftSegment route = courseApplicationService.findDraftRoute(request.toCoordinates());
        return DraftRouteWebResponse.of(route.coordinates(), route.length());
    }
}
