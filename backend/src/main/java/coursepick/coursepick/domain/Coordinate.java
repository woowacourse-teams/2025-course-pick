package coursepick.coursepick.domain;

public class Coordinate {

    private final double latitude;
    private final double longitude;


    public Coordinate(double latitude, double longitude) {
        double roundedLatitude = Math.floor(latitude * 1000000.0) / 1000000.0;
        double roundedLongitude = Math.floor(longitude * 1000000.0) / 1000000.0;

        if (roundedLatitude < -90 || roundedLatitude > 90) {
            throw new IllegalArgumentException();
        }
        if (roundedLongitude < -180 || roundedLongitude >= 180) {
            throw new IllegalArgumentException();
        }

        this.latitude = roundedLatitude;
        this.longitude = roundedLongitude;
    }

    public boolean hasSameLatitudeAndLongitude(Coordinate other) {
        // TODO : 소수점 7자리 이하로는 달라도 되는데, 요거 확인 필요
        return this.latitude == other.latitude && this.longitude == other.longitude;
    }

    public double distanceFrom(Coordinate coordinate) {
        final double earthRadiusKm = 6371.0;

        double lat1Rad = Math.toRadians(this.latitude);
        double lon1Rad = Math.toRadians(this.longitude);
        double lat2Rad = Math.toRadians(coordinate.latitude);
        double lon2Rad = Math.toRadians(coordinate.longitude);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine 공식
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadiusKm * c * 1000;
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }
}
