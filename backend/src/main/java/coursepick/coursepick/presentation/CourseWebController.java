package coursepick.coursepick.presentation;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.presentation.api.CourseWebApi;
import coursepick.coursepick.presentation.dto.CoordinateResponse;
import coursepick.coursepick.presentation.dto.GeoJson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourseWebController implements CourseWebApi {

    private final CourseApplicationService courseApplicationService;

    @Value("${admin.token}")
    private String adminToken;

    @PostMapping("/admin/courses/import")
    public void importCourses(
            @RequestParam("adminToken") String token,
            @RequestParam("filePath") String filePath
    ) {
        if (adminToken.isEmpty() || !adminToken.equals(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "올바르지 않은 어드민 토큰값 입니다.");
        }

        courseApplicationService.parseAndSaveCourses(filePath);
    }

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
        return null;
    }
}
