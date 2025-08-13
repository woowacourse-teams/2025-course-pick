package coursepick.coursepick.application.dto;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public record CourseFile(
        String name,
        CourseFileExtension extension,
        InputStream inputStream
) {
    public static CourseFile from(MultipartFile file) throws IOException {
        String[] fileFullName = file.getOriginalFilename().split("\\.");
        String fileName = fileFullName[0];
        String extension = fileFullName[1];

        return new CourseFile(fileName, CourseFileExtension.from(extension), file.getInputStream());
    }
}
