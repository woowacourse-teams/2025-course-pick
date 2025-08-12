package coursepick.coursepick.application.dto;

import java.io.InputStream;

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
    }
}
