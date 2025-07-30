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

    public static CoordinateBuilder 좌표들을_세팅한다(List<Coordinate> coordinates) {
        return new CoordinateBuilder(coordinates);
    }

    public CoordinateBuilder 첫점과_끝점의_위치가_다르면_첫점을_뒤에_추가한다() {
        List<Coordinate> connectedCoordinates = new ArrayList<>(coordinates);
        Coordinate start = coordinates.getFirst();
        Coordinate end = coordinates.getLast();
        if (!start.equals(end)) {
            connectedCoordinates.add(coordinates.getFirst());
        }
        return new CoordinateBuilder(connectedCoordinates);
    }

    public CoordinateBuilder 중복되는_점들을_제거한다() {
        List<Coordinate> distinctCoordinates = coordinates.stream()
                .distinct()
                .toList();
        return new CoordinateBuilder(distinctCoordinates);
    }

    public CoordinateBuilder 시계_반대_방향으로_정렬한다() {
        List<Coordinate> result = new ArrayList<>(coordinates);
        if (isClockwise()) {
            Collections.reverse(result);
        }
        return new CoordinateBuilder(result);
    }

    public List<Coordinate> build() {
        return Collections.unmodifiableList(coordinates);
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
