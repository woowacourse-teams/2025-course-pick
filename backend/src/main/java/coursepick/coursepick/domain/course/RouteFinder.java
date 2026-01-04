package coursepick.coursepick.domain.course;

import java.util.List;

public interface RouteFinder {

    List<Coordinate> find(Coordinate origin, Coordinate destination);
}
