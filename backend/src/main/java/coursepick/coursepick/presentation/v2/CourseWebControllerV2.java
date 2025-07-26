package coursepick.coursepick.presentation.v2;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.presentation.v2.dto.CoordinateWebResponse;
import coursepick.coursepick.presentation.v2.dto.CourseWebResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class CourseWebControllerV2 implements CourseWebApiV2 {

    private final CourseApplicationService courseApplicationService;

    @Override
    @GetMapping("/courses")
    public List<CourseWebResponse> findNearbyCourses(
            @RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude
    ) {
        List<CourseResponse> responses = courseApplicationService.findNearbyCourses(latitude, longitude);
        return CourseWebResponse.from(responses);
    }

    @Override
    @GetMapping("/courses/{id}/closest-coordinate")
    public CoordinateWebResponse findClosestCoordinate(
            @PathVariable("id") long id,
            @RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude
    ) {
        Coordinate coordinate = courseApplicationService.findClosestCoordinate(id, latitude, longitude);
        return CoordinateWebResponse.from(coordinate);
    }
}
