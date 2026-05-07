package coursepick.coursepick.presentation;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.CourseDetailResponse;
import coursepick.coursepick.application.dto.CoursesResponse;
import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.CourseFindCondition;
import coursepick.coursepick.domain.course.DraftSegment;
import coursepick.coursepick.presentation.api.CourseWebApi;
import coursepick.coursepick.presentation.dto.*;
import coursepick.coursepick.security.Login;
import coursepick.coursepick.security.UserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CourseV1WebController implements CourseWebApi {

    private final CourseApplicationService courseApplicationService;

    @Override
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

    @Override
    @GetMapping("/courses/{id}/closest-coordinate")
    public CoordinateWebResponse findClosestCoordinate(
            @PathVariable("id") String id,
            @RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude
    ) {
        Coordinate coordinate = courseApplicationService.findClosestCoordinate(id, latitude, longitude);
        return CoordinateWebResponse.from(coordinate);
    }

    @Override
    @GetMapping("/courses/{id}/route")
    public List<CoordinateWebResponse> routeToCourse(
            @PathVariable("id") String id,
            @RequestParam("startLat") double latitude,
            @RequestParam("startLng") double longitude
    ) {
        List<Coordinate> responses = courseApplicationService.routesToCourse(id, latitude, longitude);
        return CoordinateWebResponse.from(responses);
    }

    @Override
    @GetMapping("/courses/favorites")
    public List<CourseWebResponse> findFavoriteCourses(@RequestParam("courseIds") List<String> ids) {
        return courseApplicationService.findFavoriteCourses(ids).stream()
                .map(CourseWebResponse::from)
                .toList();
    }

    @Override
    @GetMapping("/courses/{id}")
    public CourseDetailWebResponse findCourseDetail(@PathVariable("id") String id) {
        CourseDetailResponse response = courseApplicationService.findCourseDetail(id);
        return CourseDetailWebResponse.from(response);
    }

    @Override
    @Login
    @PostMapping("/courses/{id}/reviews")
    public void addReview(
            @PathVariable("id") String id,
            @UserId String userId,
            @RequestBody CreateReviewWebRequest request
    ) {
        courseApplicationService.addReview(id, userId, request.content());
    }

    @Override
    @Login
    @PostMapping("/courses")
    public String addCustomCourses(@Valid @RequestBody CourseCreateWebRequest request, @UserId String userId) {
        courseApplicationService.addCustomCourse(request.name(), request.toCoordinates(), userId);
        return "코스 추가 성공";
    }

    @Override
    @PostMapping("/courses/draft/route")
    public DraftRouteWebResponse findDraftRoute(@Valid @RequestBody FindDraftRouteWebRequest request) {
        DraftSegment route = courseApplicationService.findDraftRoute(request.toCoordinates());
        return DraftRouteWebResponse.of(route.coordinates(), route.length());
    }

    @Override
    @Login
    @PostMapping("/courses/{id}/report")
    public void reportCourse(@PathVariable("id") String id, @UserId String userId) {
        courseApplicationService.report(id, userId);
    }

    @Override
    @Login
    @GetMapping("/courses/custom")
    public CoursesWebResponse findCustomCourse(
            @RequestParam(value = "userLat", required = false) Double userLatitude,
            @RequestParam(value = "userLng", required = false) Double userLongitude,
            @UserId String userId
    ) {
        CoursesResponse response = courseApplicationService.findCustomCourses(userId, userLatitude, userLongitude);
        return CoursesWebResponse.from(response);
    }
}
