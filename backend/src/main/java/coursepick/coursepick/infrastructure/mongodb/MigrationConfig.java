package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Slf4j
@Configuration
@Profile("prod")
@RequiredArgsConstructor
public class MigrationConfig {

    private final MongoTemplate mongoTemplate;

    @Bean
    public CommandLineRunner migrateToOptimizedStructure() {
        return args -> {
            log.info(">>> [운영 마이그레이션] 경로 최적화 및 Zstd 압축 시작");

            // 아직 simplifiedCoordinates 필드가 생성되지 않은 문서들을 찾습니다.
            Query query = Query.query(Criteria.where("simplifiedCoordinates").exists(false));
            List<Course> courses = mongoTemplate.find(query, Course.class);

            if (courses.isEmpty()) {
                log.info(">>> [운영 마이그레이션] 마이그레이션할 데이터가 없습니다. 이미 완료되었을 수 있습니다.");
                return;
            }

            long total = courses.size();
            long updatedCount = 0;

            for (Course course : courses) {
                try {
                    // changeCoordinates를 호출하면:
                    // 1. Douglas-Peucker 알고리즘으로 simplifiedCoordinates 생성
                    // 2. CourseWriter를 통해 저장 시 Zstd 바이너리 압축 수행
                    course.changeCoordinates(course.coordinates());
                    mongoTemplate.save(course);
                    
                    updatedCount++;
                    if (updatedCount % 50 == 0) {
                        log.info(">>> [운영 마이그레이션] 진행 중... ({}/{})", updatedCount, total);
                    }
                } catch (Exception e) {
                    log.error(">>> [운영 마이그레이션] 실패 (ID: {}): {}", course.id(), e.getMessage());
                }
            }

            log.info(">>> [운영 마이그레이션] 종료! (총 {}개 중 {}개 성공)", total, updatedCount);
        };
    }
}
