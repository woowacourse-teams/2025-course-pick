package coursepick.coursepick.test_util;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.Duration;
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

    protected RestClient anyRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(1));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(mockWebServer.url("").toString())
                .build();
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
}
