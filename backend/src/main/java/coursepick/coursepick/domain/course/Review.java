package coursepick.coursepick.domain.course;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.PersistenceCreator;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_REVIEW_CONTENT_LENGTH;

@AllArgsConstructor(access = AccessLevel.PUBLIC, onConstructor_ = @PersistenceCreator)
@Getter
@Accessors(fluent = true)
public class Review {

    public static final int MAX_CONTENT_LENGTH = 500;

    private final String id;
    private final String userId;
    private final String authorNickname;
    private final String content;
    private final int rating;
    private final Set<String> reportUserIds;
    private final Instant createdAt;

    public Review(User author, String content, int rating) {
        this(RandomStringUtils.insecure().next(10, true, true), author.id(), author.nickname().value(), content, rating, new HashSet<>(), Instant.now());
        validateContent(content);
        validateRating(rating);
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

    private static void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw ErrorType.INVALID_REVIEW_RATING.create(rating);
        }
    }
}
