package coursepick.coursepick.domain.course;

public record CourseCreator(
        String id
) {
    private static final String ADMIN_ID = "COURSE_PICK";

    public CourseCreator {
        if(id == null || id.isEmpty()) {
            id = ADMIN_ID;
        }
    }

    public static CourseCreator ofCoursePick() {
        return new CourseCreator(ADMIN_ID);
    }

    public static CourseCreator of(String userId) {
        return new CourseCreator(userId);
    }
}
