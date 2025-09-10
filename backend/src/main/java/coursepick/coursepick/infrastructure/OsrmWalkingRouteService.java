package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.WalkingRouteService;
import coursepick.coursepick.domain.Coordinate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@Profile({"dev", "prod"})
public class OsrmWalkingRouteService implements WalkingRouteService {

    private static final String REQUEST_FORMAT = "/route/v1/foot/{origin_longitude},{origin_latitude};{destination_longitude},{destination_latitude}?geometries=geojson";

    private final WebClient webClient;

    public OsrmWalkingRouteService(@Value("${osrm.url}") String osrmUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(osrmUrl)
                .build();
    }

    private static String createUriOf(Coordinate origin, Coordinate destination) {
        return REQUEST_FORMAT
                .replace("{origin_longitude}", String.valueOf(origin.longitude()))
                .replace("{origin_latitude}", String.valueOf(origin.latitude()))
                .replace("{destination_longitude}", String.valueOf(destination.longitude()))
                .replace("{destination_latitude}", String.valueOf(destination.latitude()));
    }

    private static List<Coordinate> parseResponseToCoordinates(Map<String, Object> response) {
        List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
        Map<String, Object> geometry = (Map<String, Object>) routes.get(0).get("geometry");
        List<List<Double>> coordinates = (List<List<Double>>) geometry.get("coordinates");

        return coordinates.stream()
                .map(lnglat -> new Coordinate(lnglat.get(1), lnglat.get(0)))
                .toList();
    }

    @Override
    public List<Coordinate> route(Coordinate origin, Coordinate destination) {
        Mono<Map<String, Object>> responseMono = webClient.get().uri(createUriOf(origin, destination))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });

        Map<String, Object> response = responseMono.block();

        return parseResponseToCoordinates(response);
    }
}
