package coursepick.coursepick.infrastructure.geocoding;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.logging.LogContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@Profile({"dev", "prod"})
public class KakaoReverseGeocoder implements ReverseGeocoder {

    private final RestClient kakaoLocalRestClient;
    private final String restApiKey;

    public KakaoReverseGeocoder(
            RestClient kakaoLocalRestClient,
            @Value("${kakao.rest-api-key}") String restApiKey
    ) {
        this.kakaoLocalRestClient = kakaoLocalRestClient;
        this.restApiKey = restApiKey;
    }

    @Override
    public Optional<String> findRegionName(Coordinate coordinate) {
        try {
            Map<String, Object> response = kakaoLocalRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/geo/coord2regioncode.json")
                            .queryParam("x", coordinate.longitude())
                            .queryParam("y", coordinate.latitude())
                            .build())
                    .header(AUTHORIZATION, "KakaoAK " + restApiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            return extractRegionName(response);
        } catch (Exception e) {
            log.warn("[EXCEPTION] Kakao 역지오코딩 실패", LogContent.exception(e));
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private Optional<String> extractRegionName(Map<String, Object> response) {
        List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
        if (documents == null || documents.isEmpty()) {
            return Optional.empty();
        }

        // 행정동(region_type "H")을 우선 사용하고, 없으면 첫 번째 결과를 사용한다.
        Map<String, Object> region = documents.stream()
                .filter(document -> "H".equals(document.get("region_type")))
                .findFirst()
                .orElse(documents.getFirst());

        String label = Stream.of(region.get("region_2depth_name"), region.get("region_3depth_name"))
                .map(value -> value == null ? "" : value.toString())
                .filter(value -> !value.isBlank())
                .collect(joining(" "));

        return label.isBlank() ? Optional.empty() : Optional.of(label);
    }
}
