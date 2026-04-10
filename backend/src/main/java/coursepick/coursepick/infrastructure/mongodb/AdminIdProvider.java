package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.user.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static coursepick.coursepick.application.exception.ErrorType.NOT_EXIST_USER;

@RequiredArgsConstructor
@Component
public class AdminIdProvider {

    private final MongoTemplate mongoTemplate;

    private static String ADMIN_ID;

    @PostConstruct
    public void init() {
        Query query = Query.query(Criteria.where("providerId").is("admin"));
        User adminUser = mongoTemplate.findOne(query, User.class);

        if (adminUser == null) {
            throw NOT_EXIST_USER.create("admin");
        }

        ADMIN_ID = adminUser.providerId();
    }

    public static String get() {
        return ADMIN_ID;
    }
}
