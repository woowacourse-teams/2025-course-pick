package coursepick.coursepick.auth.interceptor;

import coursepick.coursepick.application.JwtProvider;
import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.auth.AdminOnly;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final String COOKIE_NAME = "token";

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
        String token = extract(request).orElseThrow(ErrorType.NOT_EXIST_TOKEN::create);
        jwtProvider.validateToken(token);
        return true;
    }

    public Optional<String> extract(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
