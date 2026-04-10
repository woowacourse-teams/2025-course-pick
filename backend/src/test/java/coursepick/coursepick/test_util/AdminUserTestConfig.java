package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.user.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@TestConfiguration
public class AdminUserTestConfig {

    @Bean
    static BeanFactoryPostProcessor forceAdminInitOrder() {
        return beanFactory -> {
            beanFactory.getBeanDefinition("adminIdProvider")
                    .setDependsOn("adminUserInitializer");
        };
    }

    @Bean
    InitializingBean adminUserInitializer(MongoTemplate mongoTemplate) {
        return () -> mongoTemplate.save(new User(null, "admin"), "user");
    }
}
