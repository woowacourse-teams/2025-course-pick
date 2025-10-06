package coursepick.coursepick.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.presentation.dto.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Profile(value = {"dev", "prod"})
public class AuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        if (!requestUri.startsWith("/admin") || requestUri.equals("/admin/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("expiredTime") == null) {
                throw new SecurityException("로그인 정보가 없습니다.");
            }
            Long expiredTime = (Long) session.getAttribute("expiredTime");
            if (expiredTime < System.currentTimeMillis()) {
                throw new SecurityException("세션이 만료되었습니다.");
            }
        } catch (SecurityException e) {
            handleAuthenticationError(response, e);
            return;
        }
        filterChain.doFilter(request, response);
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
