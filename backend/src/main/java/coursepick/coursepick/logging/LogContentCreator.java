package coursepick.coursepick.logging;

import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class LogContentCreator {

    private static final String HTTP_LOG_FORMAT = """
            [HTTP] %s %s (%dms)
                Req Headers: %s
                Req Body: %s
                Res Status: %d
                Res Body: %s
            """;

    public static String http(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long duration) {
        String method = request.getMethod();
        String uri = extractUriWithQueryString(request);
        String headers = extractHeaderString(request);
        String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8).strip();
        if (requestBody.length() >= 100) {
            requestBody = requestBody.substring(0, 100) + "...";
        }
        int status = response.getStatus();
        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8).strip();
        if (responseBody.length() >= 100) {
            responseBody = responseBody.substring(0, 100) + "...";
        }

        return HTTP_LOG_FORMAT.formatted(method, uri, duration, headers, requestBody, status, responseBody);
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
