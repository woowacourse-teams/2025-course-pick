package coursepick.coursepick.infrastructure.coordinate_match_service;

import coursepick.coursepick.application.CoordinatesMatchService;
import coursepick.coursepick.domain.Coordinate;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Fallback
public class DummyCoordinatesMatchService implements CoordinatesMatchService {

    @Override
    public List<Coordinate> snapCoordinates(List<Coordinate> coordinates) {
        return coordinates;
    }
}
