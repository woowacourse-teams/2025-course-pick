package coursepick.coursepick.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_LATITUDE_RANGE;
import static coursepick.coursepick.application.exception.ErrorType.INVALID_LONGITUDE_RANGE;

public record Coordinate(
        double latitude,
        double longitude,
        double elevation
) {
    public Coordinate(double latitude, double longitude, double elevation) {
        double roundedLatitude = truncated(latitude);
        double roundedLongitude = truncated(longitude);
        validateLatitudeRange(roundedLatitude);
        validateLongitudeRange(roundedLongitude);
        this.latitude = roundedLatitude;
        this.longitude = roundedLongitude;
        this.elevation = elevation;
    }

    public Coordinate(double latitude, double longitude) {
        this(latitude, longitude, 0);
    }

    /**
     * 두 좌표에 대한 선형보간 좌표를 구합니다.
     * <a href="https://ko.wikipedia.org/wiki/%EC%84%A0%ED%98%95_%EB%B3%B4%EA%B0%84%EB%B2%95">선형보간</a>
     */
    public static Coordinate lerp(Coordinate start, Coordinate end, double lerpRatio) {
        double latitude = start.latitude + (end.latitude - start.latitude) * lerpRatio;
        double longitude = start.longitude + (end.longitude - start.longitude) * lerpRatio;
        double elevation = start.elevation + (end.elevation - start.elevation) * lerpRatio;
        return new Coordinate(latitude, longitude, elevation);
    }

    public boolean hasSameLatitudeAndLongitude(Coordinate other) {
        return this.latitude == other.latitude && this.longitude == other.longitude;
    }

    public double projectionRatioBetween(Coordinate lineStart, Coordinate lineEnd) {
        double startToTargetLatitudeDiff = lineStart.latitude - this.latitude;
        double startToTargetLongitudeDiff = lineStart.longitude - this.longitude;
        double startToEndLatitudeDiff = lineStart.latitude - lineEnd.latitude;
        double startToEndLongitudeDiff = lineStart.longitude - lineEnd.longitude;

        double dotProduct = startToTargetLatitudeDiff * startToEndLatitudeDiff + startToTargetLongitudeDiff * startToEndLongitudeDiff;
        double segmentLengthSquared = startToEndLatitudeDiff * startToEndLatitudeDiff + startToEndLongitudeDiff * startToEndLongitudeDiff;

        if (segmentLengthSquared == 0.0) return 0.0;
        return dotProduct / segmentLengthSquared;
    }

    public Coordinate moveTo(Coordinate other, double projectionRatio) {
        double latitudeDelta = (other.latitude - this.latitude) * projectionRatio;
        double longitudeDelta = (other.longitude - this.longitude) * projectionRatio;

        return new Coordinate(this.latitude + latitudeDelta, this.longitude + longitudeDelta, this.elevation);
    }

    private static double truncated(double value) {
        return BigDecimal.valueOf(value).setScale(7, RoundingMode.DOWN).doubleValue();
    }

    private static void validateLatitudeRange(double roundedLatitude) {
        if (roundedLatitude < -90 || roundedLatitude > 90) {
            throw INVALID_LATITUDE_RANGE.create(roundedLatitude);
        }
    }

    private static void validateLongitudeRange(double roundedLongitude) {
        if (roundedLongitude < -180 || roundedLongitude >= 180) {
            throw INVALID_LONGITUDE_RANGE.create(roundedLongitude);
        }
    }
}
