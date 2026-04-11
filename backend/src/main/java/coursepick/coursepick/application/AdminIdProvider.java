package coursepick.coursepick.application;

import coursepick.coursepick.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminIdProvider {

    private final MongoTemplate mongoTemplate;
    private String adminId;

    public synchronized String get() {
        if (adminId == null) {
            Query query = Query.query(Criteria.where("providerId").is("admin"));
            User adminUser = mongoTemplate.findOne(query, User.class);

            if (adminUser == null) {
                log.warn("어드민 유저를 찾을 수 없습니다.");
                return null;
            }

            adminId = adminUser.providerId();
            log.info("어드민 ID 초기화 완료: {}", adminId);
        }
        return adminId;
    }
}
