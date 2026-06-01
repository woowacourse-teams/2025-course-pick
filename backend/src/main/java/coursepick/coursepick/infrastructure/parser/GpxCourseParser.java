package coursepick.coursepick.infrastructure.parser;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseParser;
import coursepick.coursepick.domain.course.Gpx;
import coursepick.coursepick.domain.course.ParsedCourses;
import coursepick.coursepick.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GpxCourseParser implements CourseParser {

    @Override
    public boolean canParse(CourseFile file) {
        return file.extension() == CourseFileExtension.GPX;
    }

    @Override
    public ParsedCourses parse(CourseFile file, User user) {
        Gpx.GpxParseResult result = Gpx.from(file);

        List<Course> courses = result.gpxList().stream()
                .flatMap(gpx -> gpx.toCourses(user).stream())
                .toList();

        return new ParsedCourses(courses, result.skippedReasons());
    }
}
