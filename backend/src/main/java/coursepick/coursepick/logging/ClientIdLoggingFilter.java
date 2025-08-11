package coursepick.coursepick.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Component
@Order(HIGHEST_PRECEDENCE)
public class ClientIdLoggingFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-Client-Id";
    private static final String MDC_KEY = "client_id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientId = request.getHeader(HEADER_NAME);
        if (clientId != null && !clientId.isBlank()) {
            clientId = "Unknown";
        }

        try {
            MDC.put(MDC_KEY, clientId);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
