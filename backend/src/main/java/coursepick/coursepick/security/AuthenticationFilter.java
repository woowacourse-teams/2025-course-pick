package coursepick.coursepick.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.presentation.dto.ErrorResponse;
import coursepick.coursepick.presentation.dto.LoginWebRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_COOKIE_NAME = "ADMIN_TOKEN";
    private final String ADMIN_PASSWORD;
    private final Duration LOGIN_TTL;

    private final ObjectMapper objectMapper;
    private String currentToken = null;
    private Instant expiresAt = null;

    public AuthenticationFilter(
            @Value("${auth.admin.password}") String adminPassword,
            @Value("${auth.admin.login-ttl}") int loginTtl,
            ObjectMapper objectMapper
    ) {
        this.ADMIN_PASSWORD = adminPassword;
        this.LOGIN_TTL = Duration.ofMinutes(loginTtl);
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException {
        final String path = request.getRequestURI();
        final String method = request.getMethod();
        try {
            if ("/admin/auth/login".equals(path) && "POST".equalsIgnoreCase(method)) {
                login(request, response);
                return;
            }
            if (path.startsWith("/admin")) {
                checkAuthentication(request);
            }
        } catch (Exception exception) {
            handleError(response, exception);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoginWebRequest body = objectMapper.readValue(request.getInputStream(), LoginWebRequest.class);
        if (!ADMIN_PASSWORD.equals(body.password())) {
            clearCookie(response);
            throw ErrorType.LOGIN_FAIL.create();
        }
        final String token = UUID.randomUUID().toString();
        currentToken = token;
        expiresAt = Instant.now().plus(LOGIN_TTL);
        response.addCookie(createTokenCookie(token, (int) LOGIN_TTL.getSeconds()));
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void checkAuthentication(HttpServletRequest request) {
        String cookieToken = readCookie(request);
        if (!(cookieToken != null && cookieToken.equals(currentToken) && expiresAt != null && Instant.now().isBefore(expiresAt))) {
            throw ErrorType.TOKEN_INVALID.create();
        }
    }

    private void handleError(HttpServletResponse response, Exception exception) throws IOException {
        clearCookie(response);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.from(exception);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }

    private String readCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (TOKEN_COOKIE_NAME.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private void clearCookie(HttpServletResponse response) {
        Cookie cookie = createTokenCookie("", 0);
        response.addCookie(cookie);
    }

    private Cookie createTokenCookie(String token, int ttl) {
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, token);
        cookie.setMaxAge(ttl);
        cookie.setPath("/");
        return cookie;
    }
}

