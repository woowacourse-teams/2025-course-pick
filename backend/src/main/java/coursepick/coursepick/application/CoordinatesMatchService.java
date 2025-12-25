package coursepick.coursepick.application;

import coursepick.coursepick.domain.course.Coordinate;

import java.util.List;

public interface CoordinatesMatchService {

    List<Coordinate> snapCoordinates(List<Coordinate> coordinates);
}
