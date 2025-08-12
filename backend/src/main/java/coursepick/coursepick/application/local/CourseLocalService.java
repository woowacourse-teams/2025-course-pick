package coursepick.coursepick.application.local;

import coursepick.coursepick.application.CourseParserService;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;

import coursepick.coursepick.domain.Course;

import coursepick.coursepick.domain.CourseRepository;

import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile("local")
@RequiredArgsConstructor
public class CourseLocalService {

    private final CourseParserService courseParserService;
    private final CourseRepository courseRepository;

    @Transactional
    public void parse(List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            List<Course> courses = courseParserService.parse(FileToCourseFile(file));
            courseRepository.saveAll(courses);
        }
    }

    private CourseFile FileToCourseFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename().split("\\.")[0];
        String extension = file.getOriginalFilename().split("\\.")[1];
        return new CourseFile(fileName, CourseFileExtension.valueOf(extension.toUpperCase()), file.getInputStream());
    }
}
