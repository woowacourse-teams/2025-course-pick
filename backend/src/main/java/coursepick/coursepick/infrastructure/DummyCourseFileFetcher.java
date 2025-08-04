package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.batch.CourseFileFetcher;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Fallback
public class DummyCourseFileFetcher implements CourseFileFetcher {
    
    @Override
    public List<CourseFile> fetchNextPage() {
        return List.of();
    }
}
