package coursepick.coursepick.application.dto;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;

public record CourseFile(
        String name,
        CourseFileExtension extension,
        InputStream inputStream
) {
    public static CourseFile fromMultipartFile(MultipartFile file) throws IOException {
        String filename = Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFC);
        String extensionStr = StringUtils.getFilenameExtension(file.getOriginalFilename());
        CourseFileExtension extension = CourseFileExtension.findByName(extensionStr);
        return new CourseFile(
                filename, extension, file.getInputStream()
        );
    }
}
