package coursepick.coursepick.domain.course;

public record CourseCreator(
        String id
) {

    public static CourseCreator fromAdmin() {
        return new CourseCreator("admin");
    }

    public static CourseCreator fromUser(String userId) {
        return new CourseCreator(userId);
    }
}
