package coursepick.coursepick.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Segment(
        List<Coordinate> coordinates
) {
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

    public List<Coordinate> coordinates() {
        return Collections.unmodifiableList(coordinates);
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
