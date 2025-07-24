package coursepick.coursepick.presentation;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.presentation.api.CourseWebApi;
import coursepick.coursepick.presentation.dto.GeoJson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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
            @RequestParam("file") MultipartFile file
    ) {
        validateAdminToken(token);

        try {
            String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            courseApplicationService.parseInputStreamAndSave(file.getInputStream(), fileExtension);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private void validateAdminToken(String token) {
        if (adminToken.isEmpty() || !adminToken.equals(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "올바르지 않은 어드민 토큰값 입니다.");
        }
    }
}
