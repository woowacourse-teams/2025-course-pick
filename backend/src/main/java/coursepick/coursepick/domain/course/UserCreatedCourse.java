package coursepick.coursepick.domain.course;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
public class UserCreatedCourse {

    @Id
    private final String id;
    private final String userId;
    private final String courseId;
    private boolean isPublic;

    public UserCreatedCourse(String userId, String courseId, boolean isPublic) {
        this(null, userId, courseId, isPublic);
    }
}
