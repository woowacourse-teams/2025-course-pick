package coursepick.coursepick.presentation.v1;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.presentation.v1.dto.CoordinateResponse;
import coursepick.coursepick.presentation.v1.dto.GeoJson;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CourseWebControllerV1 implements CourseWebApiV1 {

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

    @Override
    @GetMapping("/courses/{id}/closest-coordinate")
    public CoordinateResponse findClosestCoordinate(
            @PathVariable("id") long id,
            @RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude
    ) {
        Coordinate coordinate = courseApplicationService.findClosestCoordinate(id, latitude, longitude);
        return CoordinateResponse.from(coordinate);
    }
}
