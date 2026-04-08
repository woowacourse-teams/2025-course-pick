package coursepick.coursepick.domain.course;

public record CourseOrigin(
        OriginType type,
        String id
) {

    public static CourseOrigin byAdmin() {
        return new CourseOrigin(OriginType.ADMIN, OriginType.ADMIN.name());
    }

    public static CourseOrigin byUser(String userId) {
        return new CourseOrigin(OriginType.USER, userId);
    }

    private enum OriginType {
        ADMIN, USER
    }
}
