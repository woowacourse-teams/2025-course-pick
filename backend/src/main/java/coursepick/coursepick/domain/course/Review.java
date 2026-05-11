package coursepick.coursepick.domain.course;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceCreator;

import java.time.Instant;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_REVIEW_CONTENT_LENGTH;

@AllArgsConstructor(access = AccessLevel.PUBLIC, onConstructor_ = @PersistenceCreator)
@Builder(builderMethodName = "testBuilder")
@Getter
@Accessors(fluent = true)
public class Review {

    public static final int MAX_CONTENT_LENGTH = 500;

    private final String authorNickname;
    private final String content;
    private final Instant createdAt;

    public static Review create(String authorNickname, String content) {
        validateContent(content);
        return new Review(
                authorNickname,
                content,
                Instant.now()
        );
    }

    private static void validateContent(String content) {
        if (content == null || content.isEmpty() || content.length() > MAX_CONTENT_LENGTH) {
            int length = content == null ? 0 : content.length();
            throw INVALID_REVIEW_CONTENT_LENGTH.create(length);
        }
    }
}
