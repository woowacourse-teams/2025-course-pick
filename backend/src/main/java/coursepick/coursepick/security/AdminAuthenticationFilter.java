package coursepick.coursepick.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class AdminAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_COOKIE_KEY = "admin-token";

    @Value("${admin.token}")
    private String adminToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        if (requestUri.equals("/admin/login")
                || requestUri.equals("/admin/api/login")
                || !requestUri.startsWith("/admin")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            response.sendRedirect("/admin/login");
            return;
        }

        Optional<Cookie> tokenCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(TOKEN_COOKIE_KEY))
                .findAny();

        if (tokenCookie.isEmpty()) {
            response.sendRedirect("/admin/login");
            return;
        }

        if (!adminToken.equals(tokenCookie.get().getValue())) {
            response.sendRedirect("/admin/login");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
