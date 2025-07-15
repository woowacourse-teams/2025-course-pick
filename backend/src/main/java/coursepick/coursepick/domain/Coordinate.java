package coursepick.coursepick.domain;

public class Coordinate {

    private final double latitude;
    private final double longitude;

    public Coordinate(double latitude, double longitude) {
        double roundedLatitude = Math.floor(latitude * 1000000.0) / 1000000.0;
        double roundedLongitude = Math.floor(longitude * 1000000.0) / 1000000.0;
        validateLatitudeRange(roundedLatitude);
        validateLongitudeRange(roundedLongitude);
        this.latitude = roundedLatitude;
        this.longitude = roundedLongitude;
    }

    public boolean hasSameLatitudeAndLongitude(Coordinate other) {
        return this.latitude == other.latitude && this.longitude == other.longitude;
    }

    /**
     * 두 점 사이의 거리를 계산합니다.
     * 3D 구면체 위에서의 곡률 거리를 계산하는 하버사인 공식을 활용하여 계산합니다.
     * 하버사인 공식 : https://en.wikipedia.org/wiki/Haversine_formula
     *
     * @param other 기준 좌표
     * @return 거리(M)
     */
    public double distanceFrom(Coordinate other) {
        final double earthRadiusMeter = 6371000.0;

        double lat1Rad = Math.toRadians(this.latitude);
        double lon1Rad = Math.toRadians(this.longitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double lon2Rad = Math.toRadians(other.longitude);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine 공식
        double harversineA = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double centralAngle = 2 * Math.atan2(Math.sqrt(harversineA), Math.sqrt(1 - harversineA));

        return centralAngle * earthRadiusMeter;
    }

    public double calculateDistanceRatioBetween(Coordinate start, Coordinate end) {
        double startToTargetLatitudeDiff = start.latitude - this.latitude;
        double startToTargetLongitudeDiff = start.longitude - this.longitude;
        double startToEndLatitudeDiff = start.latitude - end.latitude;
        double startToEndLongitudeDiff = start.longitude - end.longitude;

        double dotProduct = startToTargetLatitudeDiff * startToEndLatitudeDiff + startToTargetLongitudeDiff * startToEndLongitudeDiff;
        double segmentLengthSquared = startToEndLatitudeDiff * startToEndLatitudeDiff + startToEndLongitudeDiff * startToEndLongitudeDiff;

        return dotProduct / segmentLengthSquared;
    }

    public Coordinate moveTo(Coordinate other, double distanceRatio) {
        double projectionLatitude = this.latitude + (other.latitude - this.latitude) * distanceRatio;
        double projectionLongitude = this.longitude + (other.longitude - this.longitude) * distanceRatio;
        return new Coordinate(projectionLatitude, projectionLongitude);
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }

    private static void validateLatitudeRange(double roundedLatitude) {
        if (roundedLatitude < -90 || roundedLatitude > 90) {
            throw new IllegalArgumentException();
        }
    }

    private static void validateLongitudeRange(double roundedLongitude) {
        if (roundedLongitude < -180 || roundedLongitude >= 180) {
            throw new IllegalArgumentException();
        }
    }
}
