package coursepick.coursepick.application.dto;

import java.util.Arrays;

public enum CourseFileExtension {
    GPX("gpx"),
    KML("kml"),
    ;

    private final String strName;

    CourseFileExtension(String strName) {
        this.strName = strName;
    }

    public static CourseFileExtension findByName(String strName) {
        return Arrays.stream(CourseFileExtension.values())
                .filter(extension -> strName.equalsIgnoreCase(extension.strName))
                .findFirst()
                .orElseThrow();
    }
}
