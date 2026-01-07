package coursepick.coursepick.domain.course;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCreatedCourse {

    @Id
    private final String id;
    private final String userId;
    private final String courseId;
    private boolean visibility;

    public UserCreatedCourse(String userId, String courseId, boolean visibility) {
        this(null, userId, courseId, visibility);
    }
}
