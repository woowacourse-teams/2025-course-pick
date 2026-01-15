package coursepick.coursepick.infrastructure.snapper;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.CoordinateSnapper;
import coursepick.coursepick.logging.LogContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class OsrmCoordinateSnapper implements CoordinateSnapper {

    private final RestClient osrmRestClient;

    @Override
    public List<Coordinate> snap(List<Coordinate> coordinates) {
        if (coordinates.size() < 2) {
            return coordinates;
        }

        String coordinatesParam = coordinates.stream()
                .map(coordinate -> coordinate.longitude() + "," + coordinate.latitude())
                .collect(Collectors.joining(";"));

        try {
            Map<String, Object> response = osrmRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/match/v1/foot/{coordinates}")
                            .queryParam("geometries", "geojson")
                            .queryParam("overview", "full")
                            .queryParam("skip_waypoints", "true")
                            .queryParam("gaps", "ignore")
                            .queryParam("generate_hints", "false")
                            .queryParam("radiuses", generateRadiuses(coordinates.size()))
                            .queryParam("timestamps", generateTimestamps(coordinates.size()))
                            .build(coordinatesParam)
                    )
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            log.info("OSRM Match 결과: {}", response);
            return parseMatchResponse(response, coordinates);
        } catch (Exception e) {
            log.warn("[EXCEPTION] OSRM 좌표 매칭 실패", LogContent.exception(e));
            return coordinates;
        }
    }

    private List<Coordinate> parseMatchResponse(Map<String, Object> response, List<Coordinate> originals) {
        try {
            List<Map<String, Object>> matchings = (List<Map<String, Object>>) response.get("matchings");
            Map<String, Object> geometry = (Map<String, Object>) matchings.get(0).get("geometry");
            List<List<Double>> coordinates = (List<List<Double>>) geometry.get("coordinates");

            return coordinates.stream()
                    .map(coord -> new Coordinate(coord.get(1), coord.get(0)))
                    .toList();
        } catch (Exception e) {
            log.warn("[EXCEPTION] OSRM Match 응답 파싱 실패", LogContent.exception(e));
            return originals;
        }
    }

    private String generateRadiuses(int size) {
        return String.join(";", Collections.nCopies(size, "100"));
    }

    private String generateTimestamps(int size) {
        long epochSecond = Instant.now().getEpochSecond();
        return IntStream.range(0, size)
                .mapToObj(i -> String.valueOf(epochSecond + (i * 5L)))
                .collect(Collectors.joining(";"));
    }
}
