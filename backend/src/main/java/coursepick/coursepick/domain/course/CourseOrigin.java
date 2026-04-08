package coursepick.coursepick.domain.course;

public record CourseOrigin(
        String id
) {

    public static CourseOrigin byAdmin() {
        return new CourseOrigin("admin");
    }

    public static CourseOrigin byUser(String userId) {
        return new CourseOrigin(userId);
    }
}
