package coursepick.coursepick.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@Profile({"dev", "prod"})
public class RestClientConfig {

    @Bean(name = "kakaoRestClient")
    public RestClient kakaoRestClient(
            @Value("${kakao.connect-timeout:1}") int connectTimeoutSeconds,
            @Value("${kakao.read-timeout:5}") int readTimeoutSeconds
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(connectTimeoutSeconds));
        requestFactory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl("https://kapi.kakao.com")
                .build();
    }

    @Bean(name = "osrmRestClient")
    public RestClient osrmRestClient(
            @Value("${osrm.url}") String osrmUrl,
            @Value("${osrm.connect-timeout:1}") int connectTimeoutSeconds,
            @Value("${osrm.read-timeout:5}") int readTimeoutSeconds
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(connectTimeoutSeconds));
        requestFactory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(osrmUrl)
                .build();
    }
}
