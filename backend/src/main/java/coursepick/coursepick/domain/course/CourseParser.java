package coursepick.coursepick.domain.course;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.domain.user.User;

import java.util.List;

public interface CourseParser {

    boolean canParse(CourseFile file);

    ParsedCourses parse(CourseFile file, User user);
}
