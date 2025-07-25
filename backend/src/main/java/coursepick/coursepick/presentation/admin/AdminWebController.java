package coursepick.coursepick.presentation.admin;

import coursepick.coursepick.application.CourseApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AdminWebController {

    private final CourseApplicationService courseApplicationService;

    @Value("${admin.token}")
    private String adminToken;

    @Operation(hidden = true)
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

    private void validateAdminToken(String token) {
        if (adminToken.isEmpty() || !adminToken.equals(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "올바르지 않은 어드민 토큰값 입니다.");
        }
    }
}
