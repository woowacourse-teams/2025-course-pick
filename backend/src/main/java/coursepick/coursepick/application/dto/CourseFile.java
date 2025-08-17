package coursepick.coursepick.application.dto;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static java.text.Normalizer.Form;
import static java.text.Normalizer.normalize;

public record CourseFile(
        String name,
        CourseFileExtension extension,
        InputStream inputStream
) {
    public static CourseFile from(MultipartFile file) throws IOException {
        String[] fileFullName = file.getOriginalFilename().split("\\.");
        String fileName = normalize(fileFullName[0], Form.NFC);
        String extension = fileFullName[1];

        return new CourseFile(fileName, CourseFileExtension.from(extension), file.getInputStream());
    }

    public CourseFile {
        String extensionSuffix = "." + extension.name().toLowerCase();
        if (name.toLowerCase().endsWith(extensionSuffix)) {
            name = name.substring(0, name.length() - extensionSuffix.length());
        }
    }
}
