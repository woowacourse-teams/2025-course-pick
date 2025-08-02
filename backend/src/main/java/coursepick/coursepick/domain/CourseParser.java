package coursepick.coursepick.domain;

import coursepick.coursepick.application.dto.CourseFile;

import java.util.List;

public interface CourseParser {

    boolean canParse(CourseFile file);

    List<Course> parse(CourseFile file);
}
