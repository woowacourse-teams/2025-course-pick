package coursepick.coursepick.domain.course;

import coursepick.coursepick.domain.user.User;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceCreator;

import java.time.Instant;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_REVIEW_CONTENT_LENGTH;

@Getter
@Accessors(fluent = true)
public class Review {

    public static final int MAX_CONTENT_LENGTH = 500;

    private final String authorNickname;
    private final String content;
    private final Instant createdAt;

    @PersistenceCreator
    public Review(String authorNickname, String content, Instant createdAt) {
        validateContent(content);
        this.authorNickname = authorNickname;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Review(User author, String content) {
        this(author.nickname(), content, Instant.now());
    }

    private static void validateContent(String content) {
        if (content == null || content.isEmpty() || content.length() > MAX_CONTENT_LENGTH) {
            int length = content == null ? 0 : content.length();
            throw INVALID_REVIEW_CONTENT_LENGTH.create(length);
        }
    }
}
