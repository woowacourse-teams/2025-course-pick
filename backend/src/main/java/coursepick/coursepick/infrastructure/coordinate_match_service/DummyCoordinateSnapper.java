package coursepick.coursepick.infrastructure.coordinate_match_service;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.CoordinateSnapper;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Fallback
public class DummyCoordinateSnapper implements CoordinateSnapper {

    @Override
    public List<Coordinate> snap(List<Coordinate> coordinates) {
        return coordinates;
    }
}
