package coursepick.coursepick.domain;

import coursepick.coursepick.infrastructure.GeoLineListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public record Segment(
        @Convert(converter = GeoLineListConverter.class)
        @Column(columnDefinition = "TEXT")
        List<GeoLine> lines
) {
    public static Segment create(Coordinate start, Coordinate end) {
        return new Segment(List.of(GeoLine.between(start, end)));
    }

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
        return InclineType.of(lines.getFirst().start(), lines.getLast().end());
    }

    public Meter length() {
        Meter total = Meter.zero();
        for (GeoLine line : lines) {
            total = total.add(line.length());
        }
        return total;
    }

    public Coordinate closestCoordinateFrom(Coordinate target) {
        Coordinate closestCoordinate = lines.getFirst().start();
        Meter minDistance = Meter.max();

        for (GeoLine line : lines) {
            Coordinate closestCoordinateOnLine = line.closestCoordinateFrom(target);
            Meter distanceOnLine = GeoLine.between(target, closestCoordinateOnLine).length();
            if (distanceOnLine.isWithin(minDistance)) {
                minDistance = distanceOnLine;
                closestCoordinate = closestCoordinateOnLine;
            }
        }

        return closestCoordinate;
    }

    public List<Coordinate> coordinates() {
        List<Coordinate> coordinates = new ArrayList<>();
        for (GeoLine line : lines) {
            coordinates.add(line.start());
        }
        coordinates.add(lines.getLast().end());
        return coordinates;
    }

    private enum Direction {
        UP,
        DOWN,
        STRAIGHT
    }

    private Direction direction() {
        double startElevation = lines.getFirst().start().elevation();
        double endElevation = lines.getLast().end().elevation();
        if (startElevation < endElevation) {
            return Direction.UP;
        } else if (startElevation > endElevation) {
            return Direction.DOWN;
        } else {
            return Direction.STRAIGHT;
        }
    }

    private Segment merge(Segment other) {
        List<GeoLine> mergedCoordinates = new ArrayList<>();
        mergedCoordinates.addAll(this.lines);
        mergedCoordinates.addAll(other.lines);
        return new Segment(mergedCoordinates);
    }
}
