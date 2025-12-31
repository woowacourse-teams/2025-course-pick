package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.user.Authentication;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import coursepick.coursepick.presentation.WebExceptionHandler;
import coursepick.coursepick.security.AuthorizationFlowTestController;
import coursepick.coursepick.security.LoginInterceptor;
import coursepick.coursepick.security.UserIdArgumentResolver;
import coursepick.coursepick.security.WebConfig;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@SpringBootTest(classes = {
        WebConfig.class,
        LoginInterceptor.class,
        UserIdArgumentResolver.class,
        WebExceptionHandler.class,
        AuthorizationFlowTestController.class
})
@EnableWebMvc
@AutoConfigureMockMvc
public class AbstractSecurityTest {

    @Autowired
    protected MockMvc mockMvc;
    protected String userId;
    protected String accessToken;

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    @PostConstruct
    void init() {
        userId = UUID.randomUUID().toString();
        accessToken = Authentication.auth(
                Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8)),
                new User(userId, UserProvider.KAKAO, "")
        ).accessToken();
    }
}
