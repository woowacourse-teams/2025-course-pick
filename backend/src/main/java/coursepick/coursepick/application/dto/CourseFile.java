package coursepick.coursepick.application.dto;

import java.io.InputStream;

public record CourseFile(
        String name,
        CourseFileExtension extension,
        InputStream inputStream
) {
}
