package coursepick.coursepick.batch;

import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseWriter implements ItemWriter<Course> {

    private final CourseRepository courseRepository;

    @Override
    public void write(Chunk<? extends Course> chunk) throws Exception {
        courseRepository.saveAll(chunk);
    }
}
