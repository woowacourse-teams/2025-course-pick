package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.CoordinatesMatchService;
import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.GeoLine;
import coursepick.coursepick.domain.course.Meter;
import coursepick.coursepick.logging.LogContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class OsrmCoordinatesMatchService implements CoordinatesMatchService {

    private final RestClient osrmRestClient;

    @Override
    public List<Coordinate> snapCoordinates(List<Coordinate> coordinates) {
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
                            .queryParam("generate_hints", "false")
                            .build(coordinatesParam)
                    )
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

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
                    .map(coord -> createCoordinateWithElevation(
                            new Coordinate(coord.get(1), coord.get(0)),
                            originals)
                    )
                    .toList();
        } catch (Exception e) {
            log.warn("[EXCEPTION] OSRM Match 응답 파싱 실패", LogContent.exception(e));
            return originals;
        }
    }

    private Coordinate createCoordinateWithElevation(Coordinate matched,
                                                     List<Coordinate> originals) {
        Coordinate closestWithElevation = null;
        Meter minDistance = new Meter(Double.MAX_VALUE);

        for (int i = 0; i < originals.size() - 1; i++) {
            Coordinate start = originals.get(i);
            Coordinate end = originals.get(i + 1);
            GeoLine line = new GeoLine(start, end);

            Coordinate closest = line.closestCoordinateFrom(matched);

            Meter distance = new GeoLine(closest, matched).length();

            if (distance.isWithin(minDistance)) {
                minDistance = distance;
                closestWithElevation = closest;
            }
        }

        return new Coordinate(
                matched.latitude(),
                matched.longitude(),
                closestWithElevation != null ? closestWithElevation.elevation() : 0.0
        );
    }
}
