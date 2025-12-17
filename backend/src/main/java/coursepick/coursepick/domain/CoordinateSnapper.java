package coursepick.coursepick.domain;

import java.util.List;

public interface CoordinateSnapper {

    List<Coordinate> snap(List<Coordinate> coordinates);
}
