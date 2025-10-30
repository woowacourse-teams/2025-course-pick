package coursepick.coursepick.presentation;

import coursepick.coursepick.application.CourseParserService;
import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import coursepick.coursepick.presentation.dto.AdminCourseWebResponse;
import coursepick.coursepick.presentation.dto.AdminLoginWebRequest;
import coursepick.coursepick.presentation.dto.CourseRelaceWebRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminWebController {

    private static final String TOKEN_COOKIE_KEY = "admin-token";
    private static final String KAKAO_API_KEY_PLACEHOLDER = "KAKAO_API_KEY_PLACEHOLDER";
    private final CourseRepository courseRepository;
    private final CourseParserService courseParserService;
    @Value("${admin.token}")
    private String adminToken;
    @Value("${admin.kakao-map-api-key}")
    private String kakaoMapApiKey;

    @GetMapping("/admin/login")
    public String adminLoginPage() throws IOException {
        return loadHtmlFile("login.html");
    }

    @GetMapping("/admin/import")
    public String importFiles() throws IOException {
        return loadHtmlFile("import.html");
    }

    @GetMapping("/admin")
    public String adminPage() throws IOException {
        return loadHtmlFile("main.html").replace(KAKAO_API_KEY_PLACEHOLDER, kakaoMapApiKey);
    }

    @GetMapping("/admin/courses/edit")
    public String courseEditPage() throws IOException {
        return loadHtmlFile("edit.html").replace(KAKAO_API_KEY_PLACEHOLDER, kakaoMapApiKey);
    }

    @PostMapping("/admin/login")
    public ResponseEntity<Void> login(@RequestBody @Valid AdminLoginWebRequest request) {
        if (!adminToken.equals(request.password())) {
            throw ErrorType.INVALID_ADMIN_PASSWORD.create();
        }
        ResponseCookie tokenCookie = ResponseCookie.from(TOKEN_COOKIE_KEY, adminToken)
                .httpOnly(true)
                .maxAge(Duration.ofHours(1))
                .sameSite(Cookie.SameSite.STRICT.attributeValue())
                .path("/admin")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .build();
    }

    @GetMapping("/admin/courses/{id}")
    public AdminCourseWebResponse findCourseById(@PathVariable("id") String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(ErrorType.NOT_EXIST_COURSE::create);

        return AdminCourseWebResponse.from(course);
    }

    @PostMapping("/admin/import")
    public void importFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            try (CourseFile courseFile = CourseFile.from(file)) {
                List<Course> courses = courseParserService.parse(courseFile);
                courseRepository.saveAll(courses);
            }
        }
    }

    @PatchMapping("/admin/courses/{id}")
    public void modifyCourse(
            @PathVariable("id") String courseId,
            @RequestBody CourseRelaceWebRequest request
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(ErrorType.NOT_EXIST_COURSE::create);
        List<List<Double>> rawCoordinates = request.coordinates();

        if (rawCoordinates != null && !rawCoordinates.isEmpty()) {
            List<Coordinate> coordinates = rawCoordinates.stream()
                    .map(rawCoordinate -> new Coordinate(rawCoordinate.get(0), rawCoordinate.get(1), rawCoordinate.get(2)))
                    .toList();
            course.changeCoordinates(coordinates);
        }
        if (request.name() != null) course.changeName(request.name());
        if (request.roadType() != null) course.changeRoadType(request.roadType());

        // TODO : 분산 트랜잭션 고민
        courseRepository.save(course);
    }

    @DeleteMapping("/admin/courses/{id}")
    public void deleteCourse(@PathVariable("id") String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(ErrorType.NOT_EXIST_COURSE::create);

        // TODO : 분산 트랜잭션 고민
        courseRepository.delete(course);
    }

    private String loadHtmlFile(String filename) throws IOException {
        Resource resource = new ClassPathResource("static/admin/" + filename);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
