package coursepick.coursepick.presentation;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.presentation.api.CourseWebApi;
import coursepick.coursepick.presentation.dto.CoordinateWebResponse;
import coursepick.coursepick.presentation.dto.CourseWebResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_ADMIN_TOKEN;

@RestController
@RequiredArgsConstructor
public class CourseWebController implements CourseWebApi {

    private final CourseApplicationService courseApplicationService;
    private final JobLauncher jobLauncher;
    private final Job courseSyncJob;

    @Value("${admin.token}")
    private String adminToken;

    @Override
    @PostMapping("/admin/courses/sync")
    public ResponseEntity<String> syncCourses(@RequestParam("adminToken") String token) throws Exception {
        validateAdminToken(token);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("run.id", "manual-" + System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(courseSyncJob, jobParameters);
        return org.springframework.http.ResponseEntity.ok("Course Sync Job을 성공적으로 실행했습니다.");
    }

    @Override
    @GetMapping("/courses")
    public List<CourseWebResponse> findNearbyCourses(
            @RequestParam("mapLat") double mapLatitude,
            @RequestParam("mapLng") double mapLongitude,
            @RequestParam(value = "userLat", required = false) Double userLatitude,
            @RequestParam(value = "userLng", required = false) Double userLongitude
    ) {
        List<CourseResponse> responses = courseApplicationService.findNearbyCourses(mapLatitude, mapLongitude, userLatitude, userLongitude);
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

    private void validateAdminToken(String token) {
        if (adminToken.isEmpty() || !adminToken.equals(token)) {
            throw INVALID_ADMIN_TOKEN.create();
        }
    }
}
