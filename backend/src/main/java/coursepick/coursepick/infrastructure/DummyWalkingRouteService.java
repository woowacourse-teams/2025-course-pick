package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.WalkingRouteService;
import coursepick.coursepick.domain.course.Coordinate;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Fallback
public class DummyWalkingRouteService implements WalkingRouteService {

    @Override
    public List<Coordinate> route(Coordinate origin, Coordinate destination) {
        return List.of(origin, destination);
    }
}
