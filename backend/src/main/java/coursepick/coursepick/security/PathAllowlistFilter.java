package coursepick.coursepick.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PathAllowlistFilter extends OncePerRequestFilter {

    private static final Set<Pattern> ALLOW_URI_PATTERNS = Set.of(
            Pattern.compile("^/admin/courses/sync$"),
            Pattern.compile("^/courses$"),
            Pattern.compile("^/courses/[^/]+/closest-coordinate$")
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        boolean allowed = ALLOW_URI_PATTERNS.stream().anyMatch(pattern -> pattern.matcher(uri).matches());
        if (!allowed) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
