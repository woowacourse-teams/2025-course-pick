package coursepick.coursepick.test_util;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractMockServerTest {

    MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() {
        mockWebServer.close();
    }

    protected void mock(String responseBody) {
        mock(responseBody, 0);
    }

    protected void mock(String responseBody, long delayMillis) {
        MockResponse mockResponse = new MockResponse.Builder()
                .body(responseBody)
                .bodyDelay(delayMillis, TimeUnit.MILLISECONDS)
                .addHeader("Content-Type", "application/json")
                .build();
        mockWebServer.enqueue(mockResponse);
    }

    protected String url() {
        return mockWebServer.url("").toString();
    }
}
