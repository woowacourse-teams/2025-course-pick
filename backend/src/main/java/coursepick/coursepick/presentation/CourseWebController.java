package coursepick.coursepick.presentation;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.presentation.dto.CourseWebResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourseWebController {

    private final CourseApplicationService courseApplicationService;

    @GetMapping("/courses")
    public List<CourseWebResponse> findNearbyCourses(
            @RequestParam("mapLat") double mapLatitude,
            @RequestParam("mapLng") double mapLongitude,
            @RequestParam(value = "userLat", required = false) Double userLatitude,
            @RequestParam(value = "userLng", required = false) Double userLongitude,
            @RequestParam("scope") int scope
    ) {
        List<CourseResponse> responses = courseApplicationService.findNearbyCourses(mapLatitude, mapLongitude, userLatitude, userLongitude, scope);
        return CourseWebResponse.from(responses);
    }
}
