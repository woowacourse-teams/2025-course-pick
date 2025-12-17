package coursepick.coursepick.infrastructure.walking_route_service;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.RouteFinder;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Fallback
public class DummyRouteFinder implements RouteFinder {

    @Override
    public List<Coordinate> find(Coordinate origin, Coordinate destination) {
        return List.of(origin, destination);
    }
}
