package coursepick.coursepick.presentation;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.application.dto.CoursesResponse;
import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.CourseFindCondition;
import coursepick.coursepick.presentation.api.CourseWebApi;
import coursepick.coursepick.presentation.dto.CoordinateWebResponse;
import coursepick.coursepick.presentation.dto.CourseCreateWebRequest;
import coursepick.coursepick.presentation.dto.CourseWebResponse;
import coursepick.coursepick.presentation.dto.CoursesWebResponse;
import coursepick.coursepick.security.Login;
import coursepick.coursepick.security.UserId;
import lombok.RequiredArgsConstructor;
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
    @Login
    @PostMapping("/courses/create")
    public CourseWebResponse create(@UserId String userId, @RequestBody CourseCreateWebRequest courseCreateWebRequest) {
        List<Coordinate> coordinates = courseCreateWebRequest.coordinates().stream()
                .map(dto -> new Coordinate(dto.latitude(), dto.longitude()))
                .toList();

        CourseResponse courseResponse = courseApplicationService.create(userId, courseCreateWebRequest.name(), coordinates);

        return CourseWebResponse.from(courseResponse);
    }
}
