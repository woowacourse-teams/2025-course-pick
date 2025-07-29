package coursepick.coursepick.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Slf4j
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        long startTime = System.currentTimeMillis();
        filterChain.doFilter(requestWrapper, responseWrapper);
        long duration = System.currentTimeMillis() - startTime;
        
        doLog(requestWrapper, responseWrapper, duration);
        responseWrapper.copyBodyToResponse();
    }

    private static void doLog(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long duration) {
        String method = request.getMethod();
        String uri = extractUriWithQueryString(request);
        String headers = extractHeaderString(request);
        String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8).strip();
        int status = response.getStatus();
        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8).strip();

        String logContent = createLogContent(method, uri, duration, headers, requestBody, status, responseBody);
        log.info(logContent);
    }

    private static String extractUriWithQueryString(ContentCachingRequestWrapper request) {
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString != null) {
            requestURI += "?" + queryString;
        }
        return requestURI;
    }

    private static String extractHeaderString(ContentCachingRequestWrapper request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.append("[").append(headerName).append(": ").append(request.getHeader(headerName)).append("] ");
        }
        return headers.toString();
    }

    private static String createLogContent(String method, String uri, long duration, String headers, String requestBody, int status, String responseBody) {
        return """
                [HTTP] %s %s (%dms)
                    Req Headers: %s
                    Req Body: %s
                    Res Status: %d
                    Res Body: %s
                """.formatted(method, uri, duration, headers, requestBody, status, responseBody);
    }
}
