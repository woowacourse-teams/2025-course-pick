package coursepick.coursepick.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_LATITUDE_RANGE;
import static coursepick.coursepick.application.exception.ErrorType.INVALID_LONGITUDE_RANGE;

@Embeddable
public record Coordinate(
        @Column(nullable = false)
        double latitude,

        @Column(nullable = false)
        double longitude,

        @Column(nullable = false)
        double elevation
) {
    public Coordinate(double latitude, double longitude, double elevation) {
        double roundedLatitude = Math.floor(latitude * 1000000.0) / 1000000.0;
        double roundedLongitude = Math.floor(longitude * 1000000.0) / 1000000.0;
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
