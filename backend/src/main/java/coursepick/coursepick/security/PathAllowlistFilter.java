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
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PathAllowlistFilter extends OncePerRequestFilter {

    private static final Set<Pattern> ALLOW_URI_PATTERNS = new HashSet<>();

    public PathAllowlistFilter(Optional<RequestMappingHandlerMapping> requestMappingHandlerMapping) {
        requestMappingHandlerMapping.ifPresent(mappingHandlerMapping -> ALLOW_URI_PATTERNS.addAll(parseRequestMappingHandlerMapping(mappingHandlerMapping)));
        ALLOW_URI_PATTERNS.addAll(Set.of(
                Pattern.compile("^/images/verified_location.png$"),
                Pattern.compile("^/actuator.*$"),
                Pattern.compile("^/api-docs.html$"),
                Pattern.compile("^/v3/api-docs.*$")
        ));
    }

    private Set<Pattern> parseRequestMappingHandlerMapping(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return requestMappingHandlerMapping.getHandlerMethods().keySet().stream()
                .flatMap(info -> info.getDirectPaths().stream())
                .map(this::pathToPattern)
                .collect(Collectors.toSet());
    }

    private Pattern pathToPattern(String path) {
        // Spring의 경로 변수 {id} 등을 정규표현식 [^/]+ 로 치환
        String regex = path.replaceAll("\\{[^}]+}", "[^/]+");
        return Pattern.compile("^" + regex + "$");
    }

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
