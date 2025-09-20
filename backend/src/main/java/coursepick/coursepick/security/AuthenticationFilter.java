package coursepick.coursepick.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.application.JwtProvider;
import coursepick.coursepick.presentation.dto.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_TYPE_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        if (!requestUri.startsWith("/admin") || requestUri.startsWith("/admin/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractToken(request);
            jwtProvider.validateToken(token);
            filterChain.doFilter(request, response);
        } catch (SecurityException e) {
            handleAuthenticationError(response, e);
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_TYPE_PREFIX)) {
            throw new SecurityException("인증 토큰이 존재하지 않습니다.");
        }
        return header.substring(BEARER_TYPE_PREFIX.length());
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
