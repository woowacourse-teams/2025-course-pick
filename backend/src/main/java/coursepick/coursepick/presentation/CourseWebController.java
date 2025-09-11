package coursepick.coursepick.presentation;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.CourseSyncService;
import coursepick.coursepick.application.dto.CourseMetaData;
import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.presentation.api.CourseWebApi;
import coursepick.coursepick.presentation.dto.CoordinateWebResponse;
import coursepick.coursepick.presentation.dto.CourseMetadataWebResponse;
import coursepick.coursepick.presentation.dto.CourseWebResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_ADMIN_TOKEN;

@RestController
@RequiredArgsConstructor
public class CourseWebController implements CourseWebApi {

    private final CourseApplicationService courseApplicationService;
    private final CourseSyncService courseSyncService;

    @Value("${admin.token}")
    private String adminToken;

    @Override
    @PostMapping("/admin/courses/sync")
    public String syncCourses(@RequestParam("adminToken") String token) {
        validateAdminToken(token);
        courseSyncService.runCourseSyncJob();
        return "Course Sync Job을 성공적으로 실행했습니다.";
    }

    @Override
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

    private void validateAdminToken(String token) {
        if (adminToken.isEmpty() || !adminToken.equals(token)) {
            throw INVALID_ADMIN_TOKEN.create();
        }
    }

    @GetMapping("/courses/meta")
    public CourseMetadataWebResponse getCourseMetaData() {
        CourseMetaData courseMetaData = courseApplicationService.getCourseMetaData();
        return CourseMetadataWebResponse.from(courseMetaData);
    }
}
