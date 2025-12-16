package coursepick.coursepick.presentation.v1;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.CoursesResponse;
import coursepick.coursepick.presentation.dto.CoursesWebResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CourseV1WebController implements CourseV1WebApi {

    private final CourseApplicationService courseApplicationService;

    @Override
    @GetMapping("/courses")
    public CoursesWebResponse findNearbyCourses(
            @RequestParam("mapLat") double mapLatitude,
            @RequestParam("mapLng") double mapLongitude,
            @RequestParam(value = "userLat", required = false) Double userLatitude,
            @RequestParam(value = "userLng", required = false) Double userLongitude,
            @RequestParam("scope") int scope,
            @RequestParam(value = "page", required = false) Integer page
    ) {
        CoursesResponse response = courseApplicationService.findNearbyCourses(mapLatitude, mapLongitude, userLatitude, userLongitude, scope, page);
        return CoursesWebResponse.from(response);
    }
}
