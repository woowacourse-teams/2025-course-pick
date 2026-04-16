package coursepick.coursepick.logging;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Set;
import java.util.regex.Pattern;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.apache.commons.lang3.StringUtils.left;

@Slf4j
public class LogContent {

    private static final int MAX_BODY_LENGTH = 500;
    private static final Set<Pattern> SENSITIVE_URI_PATTERNS = Set.of(
            Pattern.compile("^/admin/login$"));


    public static Object[] http(ContentCachingRequestWrapper request, int status, long duration) {
        boolean sensitive = isSensitivePath(request);
        return new Object[]{
                kv("method", request.getMethod()),
                kv("uri", extractUriWithQueryString(request)),
                kv("status", status),
                kv("req_headers", extractHeaderString(request)),
                kv("duration_ms", duration),
                kv("req_body", extractRequestBody(request, sensitive))
        };
    }

    private static String extractRequestBody(ContentCachingRequestWrapper request, boolean isSensitive) {
        if (isSensitive) {
            return "[민감 정보 - 마스킹됨]";
        }
        String body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8).strip();
        if (body.isEmpty()) {
            return "(없음)";
        }
        return left(body, MAX_BODY_LENGTH) + (body.length() > MAX_BODY_LENGTH ? "...(생략)" : "");
    }

    private static boolean isSensitivePath(ContentCachingRequestWrapper request) {
        String uri = request.getRequestURI();
        return SENSITIVE_URI_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(uri).matches());
    }

    public static Object[] exception(Document document, Exception e) {
        return new Object[]{
                kv("document", document),
                kv("exception_class", e.getClass().getName()),
                kv("exception_message", e.getMessage()),
                e
        };
    }

    public static Object[] exception(Exception e) {
        return new Object[]{
                kv("exception_class", e.getClass().getName()),
                kv("exception_message", e.getMessage()),
                e
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
