package coursepick.coursepick.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import coursepick.coursepick.domain.Coordinate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.List;

@Component
public class KakaoRegionClient implements RegionClient {

    public static final String AUTHORIZATION_HEADER_PREFIX = "KakaoAK ";
    private final RestClient restClient;
    private final String appKey;
    private final String coordinateToRegionEndpoint;

    public KakaoRegionClient(
            @Value("${region.kakao.base-url}") String baseUrl,
            @Value("${region.kakao.app-key}") String appKey,
            @Value("${region.kakao.coordinate-to-region-endpoint}") String coordinateToRegionEndpoint
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(URI.create(baseUrl))
                .build();
        this.appKey = appKey;
        this.coordinateToRegionEndpoint = coordinateToRegionEndpoint;
    }

    @Override
    public String convertCoordinateToRegion(Coordinate coordinate) {
        ResponseEntity<RegionResponse> responseEntity = restClient.get().uri(uriBuilder ->
                        uriBuilder.path(coordinateToRegionEndpoint)
                                .queryParam("y", coordinate.latitude())
                                .queryParam("x", coordinate.longitude())
                                .build()
                )
                .header("Authorization", AUTHORIZATION_HEADER_PREFIX + appKey)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .toEntity(RegionResponse.class);

        RegionResponse body = responseEntity.getBody();
        return body.documents.get(0).addressName();
    }

    private void handleError(HttpRequest request, ClientHttpResponse response) {
        throw new RestClientException("외부API 호출 과정에서 에러가 발생했습니다.");
    }

    private record RegionResponse(List<Document> documents) {

        private record Document(@JsonProperty("address_name") String addressName) {
        }
    }
}
