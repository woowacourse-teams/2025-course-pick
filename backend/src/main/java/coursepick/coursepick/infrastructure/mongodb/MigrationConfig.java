package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Slf4j
@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class MigrationConfig {

    private final MongoTemplate mongoTemplate;

    @Bean
    public CommandLineRunner migrateSimplifiedCoordinates() {
        return args -> {
            log.info(">>> [Migration] simplifiedCoordinates 마이그레이션을 시작합니다...");
            
            List<Course> courses = mongoTemplate.findAll(Course.class);
            long total = courses.size();
            long updatedCount = 0;

            for (Course course : courses) {
                try {
                    // 기존 coordinates를 바탕으로 simplifiedCoordinates 필드를 채웁니다.
                    // Course 엔티티 내부의 changeCoordinates 메서드가 Douglas-Peucker를 실행합니다.
                    course.changeCoordinates(course.coordinates());
                    mongoTemplate.save(course);
                    updatedCount++;
                    
                    if (updatedCount % 10 == 0) {
                        log.info(">>> [Migration] 진행 중... ({}/{})", updatedCount, total);
                    }
                } catch (Exception e) {
                    log.error(">>> [Migration] 코스 ID: {} 마이그레이션 실패: {}", course.id(), e.getMessage());
                }
            }

            log.info(">>> [Migration] 마이그레이션 완료! (총 {}개 중 {}개 성공)", total, updatedCount);
        };
    }
}
