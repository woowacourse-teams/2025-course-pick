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

    @Bean
    public RestClient kakaoRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(1));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl("https://kapi.kakao.com")
                .build();
    }

    @Bean
    public RestClient discordRestClient(@Value("${discord.webhook-url}") String webhookUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(1));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(webhookUrl)
                .build();
    }

    @Bean
    public RestClient osrmRestClient(@Value("${osrm.url}") String osrmUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(1));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(osrmUrl)
                .build();
    }

    @Bean
    public RestClient geminiRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(1));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }
}
