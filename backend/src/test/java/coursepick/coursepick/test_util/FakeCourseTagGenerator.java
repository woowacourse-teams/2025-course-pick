package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseTag;
import coursepick.coursepick.domain.course.CourseTagGenerator;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestComponent;

import java.util.ArrayList;
import java.util.List;

@TestComponent
public class FakeCourseTagGenerator implements CourseTagGenerator {

    private List<CourseTag> tagsToReturn = new ArrayList<>();
    private int callCount = 0;

    @Override
    public List<CourseTag> generate(Course course) {
        callCount++;
        return tagsToReturn;
    }

    public void setTagsToReturn(List<CourseTag> tags) {
        this.tagsToReturn = tags;
    }

    public int getCallCount() {
        return callCount;
    }

    public void reset() {
        this.tagsToReturn = new ArrayList<>();
        this.callCount = 0;
    }
}
