package coursepick.coursepick.domain.course;

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

    public static CoordinateBuilder fromSegments(List<Segment> segments) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (Segment segment : segments) {
            if (!coordinates.isEmpty()) coordinates.removeLast();
            coordinates.addAll(segment.coordinates());
        }
        return new CoordinateBuilder(coordinates);
    }

    /**
     * 비슷한 좌표들을 제거하여 좌표의 밀집도를 줄입니다.
     * <br>
     * 비슷하다는 것은, 두 좌표의 거리가 1m 이하인 것을 말합니다.
     */
    public CoordinateBuilder removeSimilar() {
        Meter minMeter = new Meter(1);
        List<Coordinate> nonSimilarCoordinates = new ArrayList<>();
        nonSimilarCoordinates.add(coordinates.getFirst());

        for (int i = 1; i < coordinates.size(); i++) {
            Coordinate lastCoordinate = nonSimilarCoordinates.getLast();
            Coordinate currentCoordinate = coordinates.get(i);
            Meter distance = GeoLine.between(lastCoordinate, currentCoordinate).length();
            if (minMeter.isWithin(distance)) {
                nonSimilarCoordinates.add(currentCoordinate);
            }
        }

        return new CoordinateBuilder(nonSimilarCoordinates);
    }

    /**
     * 갑자기 튀는 점에 대하여 최근 점들의 선형보간으로 부드럽게 만듭니다.
     * <br>
     * 튄다는 것은, 갑자기 100m 이상 벌어지는 점을 말합니다.
     */
    public CoordinateBuilder smooth() {
        Meter maxMeter = new Meter(100);
        List<Coordinate> smoothCoordinates = new ArrayList<>();
        smoothCoordinates.add(coordinates.getFirst());

        for (int i = 1; i < coordinates.size(); i++) {
            Coordinate lastCoordinate = smoothCoordinates.getLast();
            Coordinate currentCoordinate = coordinates.get(i);
            Meter distance = GeoLine.between(lastCoordinate, currentCoordinate).length();
            if (maxMeter.isWithin(distance)) {
                Coordinate lerpedCoordinate = Coordinate.lerp(lastCoordinate, currentCoordinate, maxMeter.value() / distance.value());
                smoothCoordinates.add(lerpedCoordinate);
            } else {
                smoothCoordinates.add(currentCoordinate);
            }
        }

        return new CoordinateBuilder(smoothCoordinates);
    }

    /**
     * 원형 코스를 위한 기능이었으나, GPX 파일을 미리 보정하여 넣기로 합의되며 제거되었습니다. - @yeezy-com
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
