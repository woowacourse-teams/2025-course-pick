package coursepick.coursepick.profile;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import coursepick.coursepick.infrastructure.discord.DiscordAlerter;

@SpringBootTest
@ActiveProfiles("local")
public class LocalProfileTest {

    @MockitoBean
    DiscordAlerter  discordAlerter;

    @Test
    void 로컬_프로필에서_어떤_환경변수_주입도_없이_컨텍스트가_정상적으로_뜬다() {
    }
}
