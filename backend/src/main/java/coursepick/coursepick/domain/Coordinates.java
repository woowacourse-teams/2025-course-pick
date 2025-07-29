package coursepick.coursepick.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_COORDINATE_COUNT;

public record Coordinates(
        List<Coordinate> coordinates
) {
    public Coordinates {
        if (coordinates.size() < 2) {
            throw new IllegalArgumentException(INVALID_COORDINATE_COUNT.message(coordinates.size()));
        }
    }

    public Coordinates connectStartEnd() {
        List<Coordinate> connectedCoordinates = new ArrayList<>(coordinates);
        Coordinate start = coordinates.getFirst();
        Coordinate end = coordinates.getLast();
        if (!start.equals(end)) {
            connectedCoordinates.add(coordinates.getFirst());
        }
        return new Coordinates(connectedCoordinates);
    }

    public Coordinates sortByCounterClockwise() {
        List<Coordinate> result = new ArrayList<>(coordinates);
        if (isClockwise()) {
            Collections.reverse(result);
        }
        return new Coordinates(result);
    }

    private boolean isClockwise() {
        int lowestCoordinateIndex = findLowestCoordinateIndex();
        int nextIndex = (lowestCoordinateIndex + 1) % (coordinates.size() - 1);
        return coordinates.get(lowestCoordinateIndex).isRightOf(coordinates.get(nextIndex));
    }

    private int findLowestCoordinateIndex() {
        int lowestCoordinateIndex = 0;
        double lowestLatitude = Double.MAX_VALUE;
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate coordinate = coordinates.get(i);
            if (coordinate.latitude() < lowestLatitude) {
                lowestLatitude = coordinate.latitude();
                lowestCoordinateIndex = i;
            }
        }
        return lowestCoordinateIndex;
    }
}
