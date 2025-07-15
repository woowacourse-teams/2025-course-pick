package coursepick.coursepick.domain;

public class Coordinate {

    private final double latitude;
    private final double longitude;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean hasSameLatitudeAndLongitude(Coordinate other) {
        // TODO : 소수점 7자리 이하로는 달라도 되는데, 요거 확인 필요
        return this.latitude == other.latitude && this.longitude == other.longitude;
    }
}
