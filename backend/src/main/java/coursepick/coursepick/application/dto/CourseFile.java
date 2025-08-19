package coursepick.coursepick.application.dto;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;

import static java.text.Normalizer.normalize;

public record CourseFile(
        String name,
        CourseFileExtension extension,
        InputStream inputStream
) {
    public CourseFile {
        String extensionSuffix = "." + extension.name().toLowerCase();
        if (name.toLowerCase().endsWith(extensionSuffix)) {
            name = name.substring(0, name.length() - extensionSuffix.length());
        }
        name = normalize(name, Normalizer.Form.NFC);
    }

    public static CourseFile from(MultipartFile file) throws IOException {
        String[] fileFullName = file.getOriginalFilename().split("\\.");
        String fileName = fileFullName[0];
        String extension = fileFullName[1];

        return new CourseFile(fileName, CourseFileExtension.from(extension), file.getInputStream());
    }
}
