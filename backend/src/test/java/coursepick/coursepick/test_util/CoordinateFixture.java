package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.course.Coordinate;

import java.util.List;

public abstract class CoordinateFixture {

    public static List<Coordinate> coordinates() {
        return List.of(
                new Coordinate(0, 0),
                new Coordinate(1, 1)
        );
    }
}
