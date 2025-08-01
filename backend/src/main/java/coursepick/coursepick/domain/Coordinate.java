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

    public boolean isRightOf(Coordinate other) {
        return other.longitude < this.longitude;
    }

    private static double truncated(double value) {
        final int SCALE = 7;
        return BigDecimal.valueOf(value).setScale(SCALE, RoundingMode.DOWN).doubleValue();
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
