package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.CoordinatesMatchService;
import coursepick.coursepick.domain.course.Coordinate;
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
