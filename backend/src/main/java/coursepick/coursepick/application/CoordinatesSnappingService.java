package coursepick.coursepick.application;

import coursepick.coursepick.domain.Coordinate;

import java.util.List;

public interface CoordinatesSnappingService {

    List<Coordinate> snapCoordinates(List<Coordinate> coordinates);
}
