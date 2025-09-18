package coursepick.coursepick.security.auth;

import coursepick.coursepick.application.JwtProvider;
import coursepick.coursepick.application.exception.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final String BEARER_TYPE_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    public AuthenticationInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        if (!handlerMethod.hasMethodAnnotation(AdminOnly.class)) {
            return true;
        }
        String token = extractToken(request);
        jwtProvider.validateToken(token);
        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.length() < BEARER_TYPE_PREFIX.length() || header.startsWith(BEARER_TYPE_PREFIX)) {
            throw ErrorType.NOT_EXIST_TOKEN.create();
        }
        return header.substring(BEARER_TYPE_PREFIX.length());
    }
}
