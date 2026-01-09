package coursepick.coursepick.infrastructure.snapper;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.CoordinateSnapper;
import coursepick.coursepick.domain.course.SnapResult;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Fallback
public class DummyCoordinateSnapper implements CoordinateSnapper {

    @Override
    public SnapResult snap(List<Coordinate> coordinates) {
        return new SnapResult(coordinates, 100);
    }
}
