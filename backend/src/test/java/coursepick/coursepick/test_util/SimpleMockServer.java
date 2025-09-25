package coursepick.coursepick.test_util;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SimpleMockServer implements AutoCloseable {

    MockWebServer server;

    public SimpleMockServer(String responseBody) throws IOException {
        this(responseBody, 0);
    }

    public SimpleMockServer(String responseBody, long delayMillis) throws IOException {
        server = new MockWebServer();
        server.start();
        MockResponse mockResponse = new MockResponse.Builder()
                .body(responseBody)
                .bodyDelay(delayMillis, TimeUnit.MILLISECONDS)
                .addHeader("Content-Type", "application/json")
                .build();
        server.enqueue(mockResponse);
    }

    public String url() {
        return server.url("").toString();
    }

    @Override
    public void close() {
        server.close();
    }
}
