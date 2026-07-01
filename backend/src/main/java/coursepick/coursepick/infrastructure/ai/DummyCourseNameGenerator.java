package coursepick.coursepick.infrastructure.ai;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.CourseNameGenerator;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Fallback
public class DummyCourseNameGenerator implements CourseNameGenerator {

    @Override
    public String generate(List<Coordinate> coordinates) {
        return "새로운 러닝 코스";
    }
}
