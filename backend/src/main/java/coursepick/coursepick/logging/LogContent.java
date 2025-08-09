package coursepick.coursepick.logging;

import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.apache.commons.lang3.StringUtils.left;

public class LogContent {

    public static Object[] http(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long duration) {
        String method = request.getMethod();
        String uri = extractUriWithQueryString(request);
        String headers = extractHeaderString(request);
        String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8).strip();
        int status = response.getStatus();
        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8).strip();

        return new Object[]{
                kv("method", method),
                kv("uri", uri),
                kv("status", status),
                kv("duration_ms", duration),
                kv("req_headers", headers),
                kv("req_body", left(requestBody, 100)),
                kv("res_body", left(responseBody, 100))
        };
    }

    public static Object[] exception(Exception e) {
        return new Object[]{
                kv("exception_class", e.getClass().getName()),
                kv("exception_message", e.getMessage())
        };
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
}
