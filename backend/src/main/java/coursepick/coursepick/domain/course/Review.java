package coursepick.coursepick.domain.course;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_REVIEW_CONTENT_LENGTH;

@AllArgsConstructor(access = AccessLevel.PUBLIC, onConstructor_ = @PersistenceCreator)
@Getter
@Accessors(fluent = true)
public class Review {

    public static final int MAX_CONTENT_LENGTH = 500;

    /**
     * id를 필드명으로 사용하면 Spring Data MongoDB가 _id로 자동 번역하여 코드 작성이 복잡하다.
     * 해당 케이스에서는 {@link MongoTemplate} 을 통한 조회도 불가능했기에, iid라는 네이밍을 사용한다.
     * <a href="https://docs.spring.io/spring-data/mongodb/reference/mongodb/mapping/mapping.html#mapping.conventions.id-field">관련 공식 문서</a>
     */
    private final String id;
    private final String userId;
    private final String authorNickname;
    private final String content;
    private final Set<String> reportUserIds;
    private final Instant createdAt;

    public Review(User author, String content) {
        this(UUID.randomUUID().toString(), author.id(), author.nickname().value(), content, new HashSet<>(), Instant.now());
        validateContent(content);
    }

    public void addReport(User user) {
        if (reportUserIds.contains(user.id())) {
            throw ErrorType.ALREADY_REPORTED_REVIEW.create(this.id, user.id());
        }
        reportUserIds.add(user.id());
    }

    private static void validateContent(String content) {
        if (content == null || content.isEmpty() || content.length() > MAX_CONTENT_LENGTH) {
            int length = content == null ? 0 : content.length();
            throw INVALID_REVIEW_CONTENT_LENGTH.create(length);
        }
    }
}
