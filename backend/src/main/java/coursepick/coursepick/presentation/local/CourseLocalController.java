package coursepick.coursepick.presentation.local;

import coursepick.coursepick.application.local.CourseLocalService;

import java.io.IOException;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Profile("local")
@RequiredArgsConstructor
public class CourseLocalController {

    private final CourseLocalService courseLocalService;

    @PostMapping("/admin/courses/import")
    public String importGpxFileToCourse(@RequestParam("files") List<MultipartFile> files) throws IOException {
        courseLocalService.parse(files);
        return "done";
    }
}
