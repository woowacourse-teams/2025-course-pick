package coursepick.coursepick.presentation;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.presentation.dto.CourseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CourseWebController {

    private final CourseApplicationService courseApplicationService;

    public CourseWebController(CourseApplicationService courseApplicationService) {
        this.courseApplicationService = courseApplicationService;
    }

    @GetMapping("/courses")
    public List<CourseResponse> findNearbyCourses(
            @RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude
    ) {
        List<Course> nearbyCourses = courseApplicationService.findNearbyCourses(latitude, longitude);
        return CourseResponse.from(nearbyCourses, latitude, longitude);
    }
}
