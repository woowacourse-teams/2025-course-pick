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

    public CoordinateBuilder addFirstCoordinateIfNotConnected() {
        List<Coordinate> connectedCoordinates = new ArrayList<>(coordinates);
        Coordinate start = coordinates.getFirst();
        Coordinate end = coordinates.getLast();
        if (!start.equals(end)) {
            connectedCoordinates.add(coordinates.getFirst());
        }
        return new CoordinateBuilder(connectedCoordinates);
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

    public List<Coordinate> build() {
        return Collections.unmodifiableList(coordinates);
    }
}
