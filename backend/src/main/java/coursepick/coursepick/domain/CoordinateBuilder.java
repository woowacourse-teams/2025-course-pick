package coursepick.coursepick.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_COORDINATE_COUNT;

public class CoordinateBuilder {

    private final List<Coordinate> coordinates;

    private CoordinateBuilder(List<Coordinate> coordinates) {
        if (coordinates.size() < 2) {
            throw INVALID_COORDINATE_COUNT.create(coordinates.size());
        }
        this.coordinates = coordinates;
    }

    public static CoordinateBuilder fromRawCoordinates(List<Coordinate> coordinates) {
        return new CoordinateBuilder(coordinates);
    }

    public CoordinateBuilder removeDuplicatedCoordinate() {
        List<Coordinate> nonDuplicatedCoordinates = new ArrayList<>();
        nonDuplicatedCoordinates.add(coordinates.getFirst());
        for (int i = 1; i < coordinates.size(); i++) {
            Coordinate lastCoordinate = nonDuplicatedCoordinates.getLast();
            Coordinate currentCoordinate = this.coordinates.get(i);
            if (!lastCoordinate.equals(currentCoordinate)) {
                nonDuplicatedCoordinates.add(currentCoordinate);
            }
        }
        return new CoordinateBuilder(nonDuplicatedCoordinates);
    }

    /**
     * 원형 코스를 위한 기능이었으나, GPX 파일을 미리 보정하여 넣기로 합의되며 제거됨 - @yeezy-com
     */
    @Deprecated
    public CoordinateBuilder addFirstCoordinateIfNotConnected() {
        List<Coordinate> connectedCoordinates = new ArrayList<>(coordinates);
        Coordinate start = coordinates.getFirst();
        Coordinate end = coordinates.getLast();
        if (!start.equals(end)) {
            connectedCoordinates.add(coordinates.getFirst());
        }
        return new CoordinateBuilder(connectedCoordinates);
    }

    public List<Coordinate> build() {
        return Collections.unmodifiableList(coordinates);
    }
}
