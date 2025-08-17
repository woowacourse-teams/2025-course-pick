package coursepick.coursepick.devtool;

import coursepick.coursepick.application.CourseParserService;
import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Profile("local")
@RequiredArgsConstructor
public class CourseImportApi {

    private static final String IMPORT_PAGE = """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8"/>
                <title>파일 업로드</title>
            </head>
            <body>
            <h1>파일 업로드 폼</h1>
            <form action="/import" method="post" enctype="multipart/form-data">
                <label for="files">파싱할 파일 전송:</label>
                <input type="file" id="files" name="files" multiple required />

                <button type="submit">전송</button>
            </form>
            </body>
            </html>
            """;

    private final CourseParserService courseParserService;
    private final CourseRepository courseRepository;

    @GetMapping("/import")
    public String importPage() {
        return IMPORT_PAGE;
    }

    @PostMapping("/import")
    public String importGpxFileToCourse(@RequestParam("files") List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            List<Course> courses = courseParserService.parseAndCloseInputStream(CourseFile.from(file));
            courseRepository.saveAll(courses);
        }
        return "done";
    }
}
