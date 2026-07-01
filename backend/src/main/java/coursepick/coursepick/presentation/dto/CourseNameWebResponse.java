package coursepick.coursepick.presentation.dto;

public record CourseNameWebResponse(
        String name
) {
    public static CourseNameWebResponse from(String name) {
        return new CourseNameWebResponse(name);
    }
}
