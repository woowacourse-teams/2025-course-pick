package coursepick.coursepick.infrastructure;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OsrmRestClient {

    private final RestClient restClient;

    public OsrmRestClient(@Value("${osrm.url}") String osrmUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(1));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(osrmUrl)
                .build();
    }

    public RestClient.RequestHeadersUriSpec<?> get() {
        return restClient.get();
    }
}
