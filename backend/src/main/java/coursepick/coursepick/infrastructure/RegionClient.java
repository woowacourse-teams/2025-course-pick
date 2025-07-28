package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.Coordinate;

public interface RegionClient {

    String convertCoordinateToRegion(Coordinate coordinate);
}
