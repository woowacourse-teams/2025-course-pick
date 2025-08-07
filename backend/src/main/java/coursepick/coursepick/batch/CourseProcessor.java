package coursepick.coursepick.batch;

import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseProcessor implements ItemProcessor<Course, Course> {

    private final CourseRepository courseRepository;

    @Override
    public Course process(Course item) {
        boolean exists = courseRepository.existsByName(item.name());
        return exists ? null : item;
    }
}
