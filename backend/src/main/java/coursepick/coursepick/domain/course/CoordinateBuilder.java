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
     * Douglas-Peucker 알고리즘을 사용하여 경로를 단순화합니다.
     * <br>
     * 오차 범위(tolerance) 내에 있는 점들을 제거하여 꼭짓점 수를 줄입니다.
     */
    public CoordinateBuilder simplify(Meter tolerance) {
        if (coordinates.size() <= 2) {
            return this;
        }

        boolean[] kept = new boolean[coordinates.size()];
        kept[0] = true;
        kept[coordinates.size() - 1] = true;

        simplifyRecursive(0, coordinates.size() - 1, tolerance, kept);

        List<Coordinate> simplified = new ArrayList<>();
        for (int i = 0; i < coordinates.size(); i++) {
            if (kept[i]) {
                simplified.add(coordinates.get(i));
            }
        }

        return new CoordinateBuilder(simplified);
    }

    private void simplifyRecursive(int first, int last, Meter tolerance, boolean[] kept) {
        if (first + 1 >= last) {
            return;
        }

        double maxDistance = -1.0;
        int maxIndex = -1;

        GeoLine line = GeoLine.between(coordinates.get(first), coordinates.get(last));

        for (int i = first + 1; i < last; i++) {
            Coordinate target = coordinates.get(i);
            Coordinate closest = line.closestCoordinateFrom(target);
            double distance = GeoLine.between(target, closest).length().value();

            if (distance > maxDistance) {
                maxDistance = distance;
                maxIndex = i;
            }
        }

        if (maxDistance > tolerance.value()) {
            kept[maxIndex] = true;
            simplifyRecursive(first, maxIndex, tolerance, kept);
            simplifyRecursive(maxIndex, last, tolerance, kept);
        }
    }

    public List<Coordinate> build() {
        return Collections.unmodifiableList(coordinates);
    }
}
