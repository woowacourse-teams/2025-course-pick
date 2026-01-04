package coursepick.coursepick.security;

import coursepick.coursepick.domain.user.Authentication;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static coursepick.coursepick.application.exception.ErrorType.AUTHENTICATION_FAIL;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    public static final String AUTH_USER_ID = "AUTH_USER_ID";
    private final SecretKey secretKey;

    public LoginInterceptor(@Value("${jwt.secret-key}") String secretKeyString) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        if (!handlerMethod.hasMethodAnnotation(Login.class)) {
            return true;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw AUTHENTICATION_FAIL.create();
        }

        String token = authorizationHeader.substring("Bearer ".length());

        Authentication authentication = new Authentication(token);
        String userId = authentication.validateAndGetUserId(secretKey);
        request.setAttribute(AUTH_USER_ID, userId);
        return true;
    }
}
