package coursepick.coursepick.security;

import coursepick.coursepick.domain.user.Authentication;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import coursepick.coursepick.presentation.WebExceptionHandler;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = {
        AuthorizationFlowTestController.class,
        WebConfig.class,
        LoginInterceptor.class,
        UserIdArgumentResolver.class,
        WebExceptionHandler.class,
        AuthorizationFlowTest.TestWebConfig.class
})
@AutoConfigureMockMvc
class AuthorizationFlowTest {

    @Value("${jwt.secret-key}")
    String secretKeyString;
    @Autowired
    MockMvc mockMvc;
    String userId = "123456789";
    String accessToken;

    @BeforeEach
    void setUp() {
        var user = new User(userId, UserProvider.KAKAO, "");
        var secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        accessToken = Authentication.auth(secretKey, user).accessToken();
    }

    @Test
    void 정상적인_토큰으로_인증한다() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test1")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 비정상적인_토큰으로_인증하면_예외가_발생한다() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test1")
                        .header("Authorization", "Bearer " + "bad-token"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void 정상적인_토큰으로_인증하고_유저ID를_추출한다() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test2")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(userId));
    }

    @Configuration
    @EnableWebMvc
    static class TestWebConfig {
    }
}
