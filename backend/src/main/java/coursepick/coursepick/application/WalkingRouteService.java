package coursepick.coursepick.application;

import coursepick.coursepick.domain.course.Coordinate;

import java.util.List;

public interface WalkingRouteService {

    List<Coordinate> route(Coordinate origin, Coordinate destination);
}
