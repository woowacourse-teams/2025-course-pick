package coursepick.coursepick.domain;

import java.util.List;

public interface RouteFinder {

    List<Coordinate> find(Coordinate origin, Coordinate destination);
}
