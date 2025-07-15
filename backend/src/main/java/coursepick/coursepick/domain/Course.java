package coursepick.coursepick.domain;

import java.util.List;

public class Course {

    private final String name;
    private final List<Coordinate> coordinates;

    public Course(String name, List<Coordinate> coordinates) {
        String compactName = compactName(name);
        validateNameLength(compactName);
        validateCoordinatesCount(coordinates);
        validateFirstLastCoordinateHasSameLatitudeAndLongitude(coordinates);
        this.name = compactName;
        this.coordinates = coordinates;
    }

    public double length() {
        double totalLength = 0;
        for (int idx = 0; idx < coordinates.size() - 1; idx++) {
            Coordinate coordinate1 = coordinates.get(idx);
            Coordinate coordinate2 = coordinates.get(idx + 1);

            totalLength += coordinate1.distanceFrom(coordinate2);
        }

        return totalLength;
    }

    public String name() {
        return name;
    }

    public double minDistanceFrom(Coordinate target) {
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate start = coordinates.get(i);
            Coordinate end = coordinates.get(i + 1);

            double distance = distanceFromPointToLineSegment(target, start, end);
            minDistance = Math.min(minDistance, distance);
        }

        return minDistance;
    }

    private double distanceFromPointToLineSegment(Coordinate point, Coordinate start, Coordinate end) {
        // 선분의 양 끝점까지의 거리
        double distToStart = point.distanceFrom(start);
        double distToEnd = point.distanceFrom(end);

        // 선분의 길이
        double segmentLength = start.distanceFrom(end);

        // 선분이 점인 경우
        if (segmentLength == 0) {
            return distToStart;
        }

        // 투영 계산을 위한 벡터 내적
        double lat1 = start.latitude();
        double lon1 = start.longitude();
        double lat2 = end.latitude();
        double lon2 = end.longitude();
        double latP = point.latitude();
        double lonP = point.longitude();

        double A = latP - lat1;
        double B = lonP - lon1;
        double C = lat2 - lat1;
        double D = lon2 - lon1;

        double dot = A * C + B * D;
        double lenSq = C * C + D * D;

        double param = dot / lenSq;

        // 투영점이 선분 밖에 있는 경우
        if (param < 0) {
            return distToStart;
        }
        if (param > 1) {
            return distToEnd;
        }

        // 투영점이 선분 위에 있는 경우
        double projLat = lat1 + param * C;
        double projLon = lon1 + param * D;
        Coordinate projection = new Coordinate(projLat, projLon);

        return point.distanceFrom(projection);
    }

    private static String compactName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    private static void validateNameLength(String compactName) {
        if (compactName.length() < 2 || compactName.length() > 30) {
            throw new IllegalArgumentException();
        }
    }

    private static void validateCoordinatesCount(List<Coordinate> coordinates) {
        if (coordinates.size() < 2) {
            throw new IllegalArgumentException();
        }
    }

    private static void validateFirstLastCoordinateHasSameLatitudeAndLongitude(List<Coordinate> coordinates) {
        if (!coordinates.getFirst().hasSameLatitudeAndLongitude(coordinates.getLast())) {
            throw new IllegalArgumentException();
        }
    }
}
