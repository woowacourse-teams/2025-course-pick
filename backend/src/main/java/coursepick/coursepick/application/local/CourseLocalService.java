package coursepick.coursepick.application.local;

import coursepick.coursepick.application.CourseParserService;
import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Profile("local")
@RequiredArgsConstructor
public class CourseLocalService {

    private final CourseParserService courseParserService;
    private final CourseRepository courseRepository;

    @Transactional
    public void parse(List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            List<Course> courses = courseParserService.parseAndCloseInputStream(CourseFile.from(file));
            courseRepository.saveAll(courses);
        }
    }
}
