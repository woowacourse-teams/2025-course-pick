package coursepick.coursepick.domain;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_COORDINATE_COUNT;

import java.util.ArrayList;
import java.util.List;

public class CoordinateFactory {

    public static List<Coordinate> create(List<Coordinate> rawCoordinates) {
        List<Coordinate> nonSimilarCoordinates = removeSimilar(rawCoordinates);
        List<Coordinate> coordinates = smooth(nonSimilarCoordinates);

        validateValidCoordinateSize(coordinates);
        return coordinates;
    }

    private static void validateValidCoordinateSize(List<Coordinate> rawCoordinates) {
        if (rawCoordinates.size() < 2) {
            throw INVALID_COORDINATE_COUNT.create(rawCoordinates.size());
        }
    }

    private static List<Coordinate> removeSimilar(List<Coordinate> rawCoordinates) {
        Meter minMeter = new Meter(1);
        List<Coordinate> nonSimilarCoordinates = new ArrayList<>();
        nonSimilarCoordinates.add(rawCoordinates.getFirst());

        for (int i = 1; i < rawCoordinates.size(); i++) {
            Coordinate lastCoordinate = nonSimilarCoordinates.getLast();
            Coordinate currentCoordinate = rawCoordinates.get(i);
            Meter distance = GeoLine.between(lastCoordinate, currentCoordinate).length();
            if (minMeter.isWithin(distance)) {
                nonSimilarCoordinates.add(currentCoordinate);
            }
        }

        return nonSimilarCoordinates;
    }

    /**
     * 갑자기 튀는 점에 대하여 최근 점들의 선형보간으로 부드럽게 만듭니다.
     * <br>
     * 튄다는 것은, 갑자기 100m 이상 벌어지는 점을 말합니다.
     */
    private static List<Coordinate> smooth(List<Coordinate> rawCoordinates) {
        Meter maxMeter = new Meter(100);
        List<Coordinate> smoothCoordinates = new ArrayList<>();
        smoothCoordinates.add(rawCoordinates.getFirst());

        for (int i = 1; i < rawCoordinates.size(); i++) {
            Coordinate lastCoordinate = smoothCoordinates.getLast();
            Coordinate currentCoordinate = rawCoordinates.get(i);
            Meter distance = GeoLine.between(lastCoordinate, currentCoordinate).length();
            if (maxMeter.isWithin(distance)) {
                Coordinate lerpedCoordinate = Coordinate.lerp(lastCoordinate, currentCoordinate, maxMeter.value() / distance.value());
                smoothCoordinates.add(lerpedCoordinate);
            } else {
                smoothCoordinates.add(currentCoordinate);
            }
        }

        return smoothCoordinates;
    }
}
