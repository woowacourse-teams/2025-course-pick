package coursepick.coursepick.profile;

import coursepick.coursepick.test_util.AdminUserTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(AdminUserTestConfig.class)
@SpringBootTest
@ActiveProfiles("local")
public class LocalProfileTest {

    @Test
    void 로컬_프로필에서_어떤_환경변수_주입도_없이_컨텍스트가_정상적으로_뜬다() {
    }
}
