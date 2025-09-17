package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.WalkingRouteService;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.logging.LogContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile({"dev", "prod"})
public class OsrmWalkingRouteService implements WalkingRouteService {

    private final RestClient restClient;

    public OsrmWalkingRouteService(@Value("${osrm.url}") String osrmUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(osrmUrl)
                .build();
    }

    @Override
    public List<Coordinate> route(Coordinate origin, Coordinate destination) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/route/v1/foot/{origin_longitude},{origin_latitude};{destination_longitude},{destination_latitude}")
                            .queryParam("geometries", "geojson")
                            .queryParam("overview", "full")
                            .queryParam("generate_hints", "false")
                            .build(origin.longitude(), origin.latitude(), destination.longitude(), destination.latitude())
                    )
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            List<Coordinate> coordinates = parseResponseToCoordinates(response);
            coordinates.addFirst(origin);
            coordinates.addLast(destination);
            return coordinates;
        } catch (Exception e) {
            log.warn("[EXCEPTION] OSRM 길찾기 실패", LogContent.exception(e));
            throw new IllegalStateException("길찾기에 실패했습니다.", e);
        }
    }

    private static List<Coordinate> parseResponseToCoordinates(Map<String, Object> response) {
        try {
            List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
            Map<String, Object> geometry = (Map<String, Object>) routes.get(0).get("geometry");
            List<List<Double>> coordinates = (List<List<Double>>) geometry.get("coordinates");

            return coordinates.stream()
                    .map(lnglat -> new Coordinate(lnglat.get(1), lnglat.get(0)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("[EXCEPTION] OSRM 응답 파싱 실패", LogContent.exception(e));
            throw new IllegalStateException("길찾기에 실패했습니다.", e);
        }
    }
}
