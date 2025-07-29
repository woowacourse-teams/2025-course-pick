package coursepick.coursepick.domain;

import coursepick.coursepick.infrastructure.CoordinateListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public record Segment(
        @Convert(converter = CoordinateListConverter.class)
        @Column(columnDefinition = "TEXT")
        List<Coordinate> coordinates
) {
    public static List<Segment> create(List<Coordinate> coordinates) {
        List<GeoLine> geoLines = GeoLine.split(coordinates);
        List<Segment> segments = geoLines.stream()
                .map(GeoLine::toSegment)
                .toList();

        List<Segment> sameDirectionSegments = Segment.mergeSameDirection(segments);
        return Segment.mergeSameInclineType(sameDirectionSegments);
    }

    // 경향성이 같은 것끼리 합친다.
    public static List<Segment> mergeSameDirection(List<Segment> segments) {
        List<Segment> mergedSegments = new ArrayList<>();
        mergedSegments.add(segments.getFirst());
        for (int i = 1; i < segments.size(); i++) {
            Segment beforeSegment = mergedSegments.removeLast();
            Segment currentSegment = segments.get(i);
            Direction beforeDirection = beforeSegment.direction();
            Direction currentDirection = currentSegment.direction();

            if (beforeDirection == currentDirection) {
                mergedSegments.add(beforeSegment.merge(currentSegment));
            } else {
                mergedSegments.add(beforeSegment);
                mergedSegments.add(currentSegment);
            }
        }
        return mergedSegments;
    }

    // 경사타입이 같은 것끼리 합친다.
    public static List<Segment> mergeSameInclineType(List<Segment> segments) {
        List<Segment> mergedSegments = new ArrayList<>();
        mergedSegments.add(segments.getFirst());
        for (int i = 1; i < segments.size(); i++) {
            Segment beforeSegment = mergedSegments.removeLast();
            Segment currentSegment = segments.get(i);
            InclineType beforeInclineType = beforeSegment.inclineType();
            InclineType currentInclineType = currentSegment.inclineType();

            if (beforeInclineType == currentInclineType) {
                mergedSegments.add(beforeSegment.merge(currentSegment));
            } else {
                mergedSegments.add(beforeSegment);
                mergedSegments.add(currentSegment);
            }
        }
        return mergedSegments;
    }

    public InclineType inclineType() {
        return InclineType.of(coordinates.getFirst(), coordinates.getLast());
    }

    public Meter length() {
        Meter total = Meter.zero();
        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate coord1 = coordinates.get(i);
            Coordinate coord2 = coordinates.get(i + 1);

            Meter meter = GeoLine.between(coord1, coord2).length();
            total = total.add(meter);
        }

        return total;
    }

    public Coordinate closestCoordinateFrom(Coordinate target) {
        Coordinate closestCoordinate = coordinates.getFirst();
        Meter minDistance = Meter.max();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            GeoLine line = GeoLine.between(coordinates.get(i), coordinates.get(i + 1));

            Coordinate closestCoordinateOnLine = line.closestCoordinateFrom(target);
            Meter distanceOnLine = GeoLine.between(target, closestCoordinateOnLine).length();
            if (distanceOnLine.isWithin(minDistance)) {
                minDistance = distanceOnLine;
                closestCoordinate = closestCoordinateOnLine;
            }
        }

        return closestCoordinate;
    }

    private enum Direction {
        UP,
        DOWN,
        STRAIGHT
    }

    private Direction direction() {
        double startElevation = coordinates.getFirst().elevation();
        double endElevation = coordinates.getLast().elevation();
        if (startElevation < endElevation) {
            return Direction.UP;
        } else if (startElevation > endElevation) {
            return Direction.DOWN;
        } else {
            return Direction.STRAIGHT;
        }
    }

    private Segment merge(Segment other) {
        ArrayList<Coordinate> mergedCoordinates = new ArrayList<>();
        mergedCoordinates.addAll(this.coordinates);
        mergedCoordinates.removeLast();
        mergedCoordinates.addAll(other.coordinates);
        return new Segment(mergedCoordinates);
    }
}
