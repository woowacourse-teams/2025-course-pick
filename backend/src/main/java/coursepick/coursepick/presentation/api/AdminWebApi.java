package coursepick.coursepick.presentation.api;

import coursepick.coursepick.presentation.dto.AdminCourseWebResponse;
import coursepick.coursepick.presentation.dto.AdminLoginWebRequest;
import coursepick.coursepick.presentation.dto.CourseRelaceWebRequest;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Hidden
public interface AdminWebApi {

    ResponseEntity<Void> login(AdminLoginWebRequest request);

    AdminCourseWebResponse findCourseById(String id);

    AdminCourseWebResponse findCourseByName(String name);

    void importFiles(List<MultipartFile> files) throws IOException;

    void modifyCourse(String courseId, CourseRelaceWebRequest request);

    void deleteCourse(String id);
}
