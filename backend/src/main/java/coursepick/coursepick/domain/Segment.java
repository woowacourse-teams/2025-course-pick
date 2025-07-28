package coursepick.coursepick.domain;

import java.util.List;

public class Segment {

    public static List<Segment> split(List<Coordinate> coordinates) {
        return null;
    }

    public static List<Segment> mergeSameInclineType(List<Segment> segments) {
        return null;
    }

    /*
    -5도 이하 다운힐
    -4도 ~ 4도 평지
    5도 이상 업힐
     */
    public InclineType inclineType() {
        return null;
    }

    public List<Coordinate> coordinates() {
        return null;
    }
}
