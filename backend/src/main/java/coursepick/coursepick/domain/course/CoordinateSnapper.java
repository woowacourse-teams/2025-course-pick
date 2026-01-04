package coursepick.coursepick.domain.course;

import java.util.List;

public interface CoordinateSnapper {

    List<Coordinate> snap(List<Coordinate> coordinates);
}
