package coursepick.coursepick.infrastructure;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Profile({"dev", "prod"})
public class OsrmRestClient {

    private final RestClient restClient;

    public OsrmRestClient(
            @Value("${osrm.url}") String osrmUrl,
            @Value("${osrm.connect-timeout:1}") int connectTimeoutSeconds,
            @Value("${osrm.read-timeout:5}") int readTimeoutSeconds) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(connectTimeoutSeconds));
        requestFactory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(osrmUrl)
                .build();
    }

    public RestClient getRestClient() {
        return restClient;
    }
}
