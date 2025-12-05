package coursepick.coursepick.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PathAllowlistFilter extends OncePerRequestFilter {

    private static final Set<Pattern> ALLOW_URI_PATTERNS = Set.of(
            Pattern.compile("^/courses$"),
            Pattern.compile("^/courses/favorites$"),
            Pattern.compile("^/courses/[^/]+/closest-coordinate$"),
            Pattern.compile("^/courses/[^/]+/route$"),
            Pattern.compile("^/actuator/*$"),
            Pattern.compile("^/api-docs.html$"),
            Pattern.compile("^/v3/api-docs.*$"),
            Pattern.compile("^/admin$"),
            Pattern.compile("^/admin/login$"),
            Pattern.compile("^/admin/import$"),
            Pattern.compile("^/admin/courses$"),
            Pattern.compile("^/admin/courses/edit$"),
            Pattern.compile("^/admin/api/login$"),
            Pattern.compile("^/admin/api/import$"),
            Pattern.compile("^/admin/api/courses$"),
            Pattern.compile("^/admin/api/courses/[^/]+$"),
            Pattern.compile("^/notices/[^/]+$"),
            Pattern.compile("^/images/verified_location.png$")
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        boolean allowed = ALLOW_URI_PATTERNS.stream().anyMatch(pattern -> pattern.matcher(uri).matches());
        if (!allowed) {
            log.warn("[SECURITY] 화이트리스트가 아닌 경로로 요청이 들어왔습니다. uri={}", uri);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
