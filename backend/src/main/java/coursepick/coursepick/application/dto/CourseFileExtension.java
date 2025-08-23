package coursepick.coursepick.application.dto;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_FILE_EXTENSION;

import java.util.Arrays;

public enum CourseFileExtension {
    GPX,
    KML,
    ;

    public static CourseFileExtension from(String extension) {
        return Arrays.stream(CourseFileExtension.values())
                .filter(ext -> extension.toUpperCase().equals(ext.toString()))
                .findFirst()
                .orElseThrow(INVALID_FILE_EXTENSION::create);
    }
}
