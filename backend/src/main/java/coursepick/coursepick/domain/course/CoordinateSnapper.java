package coursepick.coursepick.domain.course;

import java.util.List;

public interface CoordinateSnapper {

    SnapResult snap(List<Coordinate> coordinates);
}
