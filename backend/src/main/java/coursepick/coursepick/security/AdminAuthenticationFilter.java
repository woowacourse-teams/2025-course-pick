package coursepick.coursepick.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.presentation.dto.ErrorResponse;
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
import java.util.Arrays;

@Component
public class AdminAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_COOKIE_KEY = "admin-token";

    private final String adminToken;
    private final ObjectMapper objectMapper;

    public AdminAuthenticationFilter(
            @Value("${admin.token}") String adminToken,
            ObjectMapper objectMapper
    ) {
        this.adminToken = adminToken;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        if (requestUri.equals("/api/admin/login") || requestUri.equals("/admin/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!(requestUri.startsWith("/admin") || requestUri.startsWith("/api/admin"))) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String requestAdminToken = extractRequestAdminToken(request);
            validateAdminToken(requestAdminToken);
        } catch (SecurityException e) {
            handleAuthenticationError(response, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String extractRequestAdminToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            throw ErrorType.INVALID_ADMIN_TOKEN.create();
        }

        Cookie tokenCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(TOKEN_COOKIE_KEY))
                .findAny()
                .orElseThrow(ErrorType.INVALID_ADMIN_TOKEN::create);

        return tokenCookie.getValue();
    }

    private void validateAdminToken(String requestAdminToken) {
        if (adminToken.equals(requestAdminToken)) {
            return;
        }
        throw ErrorType.INVALID_ADMIN_TOKEN.create();
    }

    private void handleAuthenticationError(HttpServletResponse response, SecurityException exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.from(exception);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}
