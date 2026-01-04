package coursepick.coursepick.domain.course;

public enum InclineType {
    UPHILL,
    DOWNHILL,
    FLAT;

    /**
     * 두 좌표간 경사 타입을 계산한다.
     * DOWNHILL: ~ -5도
     * FLAT: -4도 ~ 4도
     * UPHILL: 5도 ~
     */
    public static InclineType of(Coordinate start, Coordinate end) {
        double elevationChange = end.elevation() - start.elevation();
        double distance = GeoLine.between(start, end).length().value();

        double angleInDegrees = Math.toDegrees(Math.atan2(elevationChange, distance));

        if (angleInDegrees >= 5.0) {
            return UPHILL;
        } else if (angleInDegrees <= -5.0) {
            return DOWNHILL;
        } else {
            return FLAT;
        }
    }
}
