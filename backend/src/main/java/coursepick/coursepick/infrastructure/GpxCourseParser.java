package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseParser;
import coursepick.coursepick.domain.Gpx;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GpxCourseParser implements CourseParser {

    @Override
    public boolean canParse(CourseFile file) {
        return file.extension() == CourseFileExtension.GPX;
    }

    @Override
    public List<Course> parse(CourseFile file) {
        return Gpx.from(file).toCourses();
    }
}
