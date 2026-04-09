package coursepick.coursepick.presentation;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.CoursesResponse;
import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.CourseFindCondition;
import coursepick.coursepick.domain.course.DraftSegment;
import coursepick.coursepick.presentation.api.CourseWebApi;
import coursepick.coursepick.presentation.dto.*;
import coursepick.coursepick.security.Login;
import coursepick.coursepick.security.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v2")
@RequiredArgsConstructor
public class CourseV2WebController implements CourseWebApi {

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
    @Login
    @PostMapping("/courses")
    public String addCustomCourses(@Valid @RequestBody CustomCourseWebRequest request, @UserId String userId) {

        List<List<Double>> rawCoordinates = request.coordinates();
        List<Coordinate> coordinates = rawCoordinates.stream()
                .map(rawCoordinate -> new Coordinate(rawCoordinate.getFirst(), rawCoordinate.get(1)))
                .toList();

        courseApplicationService.addCustomCourse(request.name(), coordinates, userId);

        return "코스 추가 성공";
    }

    @Override
    @PostMapping("/courses/draft/route")
    public DraftRouteWebResponse findDraftRoute(@Valid @RequestBody FindDraftRouteWebRequest request) {
        DraftSegment route = courseApplicationService.findDraftRoute(request.toCoordinates());
        return DraftRouteWebResponse.of(route.coordinates(), route.length());
    }
}
