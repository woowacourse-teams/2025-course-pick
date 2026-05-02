package coursepick.coursepick.domain.course;

import core.TrackCleaner;
import core.common.Algorithm;
import core.common.CleaningResult;
import core.common.GpsTrack;
import core.outlier.Threshold;
import core.simplifier.Tolerance;

import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_COORDINATE_COUNT;

public class CoordinateBuilder {

    private final List<RawCoordinate> coordinates;

    private CoordinateBuilder(List<RawCoordinate> coordinates) {
        if (coordinates.size() < 2) {
            throw INVALID_COORDINATE_COUNT.create(coordinates.size());
        }
        this.coordinates = coordinates;
    }

    public static CoordinateBuilder fromRawCoordinates(List<RawCoordinate> coordinates) {
        return new CoordinateBuilder(coordinates);
    }

    public static CoordinateBuilder fromCoordinates(List<Coordinate> coordinates) {
        List<RawCoordinate> rawCoordinates = coordinates.stream()
                .map(coord -> new RawCoordinate(coord.latitude(), coord.longitude(), null))
                .toList();

        return new CoordinateBuilder(rawCoordinates);
    }


    /**
     * 튄 좌표를 제거합니다.
     * <br>
     * 튄다는 것은, 흐름 속에서 threshold만큼 갑자기 튀는 좌표를 얘기합니다.
     */
    public CoordinateBuilder removeSimilar(Meter threshold) {
        List<core.common.Coordinate> externalCoordinates = convertRawToExternalCoordinates();

        CleaningResult result = TrackCleaner.of(new GpsTrack(externalCoordinates))
                .removeOutliers(Threshold.ofMeters(threshold.value()))
                .clean();

        List<RawCoordinate> rawCoordinates = extractCleanedCoordinates(result);

        return new CoordinateBuilder(rawCoordinates);
    }

    /**
     * 칼만 필터를 통해 코스의 GPS 오차로 인해 발생하는 노이즈를 제거합니다.
     */
    public CoordinateBuilder smooth() {
        List<core.common.Coordinate> externalCoordinates = convertRawToExternalCoordinates();

        CleaningResult result = TrackCleaner.of(new GpsTrack(externalCoordinates))
                .smooth(Algorithm.KALMAN)
                .clean();

        List<RawCoordinate> rawCoordinates = extractCleanedCoordinates(result);

        return new CoordinateBuilder(rawCoordinates);
    }

    /**
     * Douglas-Peucker 알고리즘을 사용하여 경로를 단순화합니다.
     * <br>
     * 오차 범위(tolerance) 내에 있는 점들을 제거하여 꼭짓점 수를 줄입니다.
     */
    public CoordinateBuilder simplify(Meter tolerance) {
        List<core.common.Coordinate> externalCoordinates = convertRawToExternalCoordinates();

        CleaningResult result = TrackCleaner.of(new GpsTrack(externalCoordinates))
                .simplify(Tolerance.ofMeters(tolerance.value()))
                .clean();

        List<RawCoordinate> rawCoordinates = extractCleanedCoordinates(result);

        return new CoordinateBuilder(rawCoordinates);
    }

    private List<RawCoordinate> extractCleanedCoordinates(CleaningResult result) {
        GpsTrack gpsTrack = result.cleanedTrack();
        List<RawCoordinate> rawCoordinates = gpsTrack.coordinates().stream()
                .map(coord -> new RawCoordinate(coord.latitude(), coord.longitude(), coord.timestamp()))
                .toList();
        return rawCoordinates;
    }

    private List<core.common.Coordinate> convertRawToExternalCoordinates() {
        return coordinates.stream()
                .map(coord -> new core.common.Coordinate(coord.longitude(), coord.latitude(), coord.timestamp()))
                .toList();
    }

    public List<Coordinate> build() {
        return coordinates.stream()
                .map(coord -> new Coordinate(coord.latitude(), coord.longitude()))
                .toList();
    }
}
