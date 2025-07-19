package coursepick.coursepick.presentation;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.presentation.api.CourseWebApi;
import coursepick.coursepick.presentation.dto.GeoJson;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourseWebController implements CourseWebApi {

    private final CourseApplicationService courseApplicationService;

    @Override
    @GetMapping("/courses")
    public List<GeoJson> findNearbyCourses(
            @RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude
    ) {
        List<CourseResponse> responses = courseApplicationService.findNearbyCourses(latitude, longitude);
        return GeoJson.from(responses);
    }
}
