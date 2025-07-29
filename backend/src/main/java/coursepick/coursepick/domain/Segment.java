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
    public static List<Segment> create(Coordinates coordinates) {
        List<GeoLine> geoLines = GeoLine.split(coordinates);
        List<Segment> segments = geoLines.stream()
                .map(GeoLine::toSegment)
                .toList();

        List<Segment> sameDirectionSegments = Segment.mergeSameDirection(segments);
        return Segment.mergeSameInclineType(sameDirectionSegments);
    }

    public InclineType inclineType() {
        return InclineType.of(startCoordinate(), endCoordinate());
    }

    public Meter length() {
        Meter total = Meter.zero();
        for (GeoLine line : lines) {
            total = total.add(line.length());
        }
        return total;
    }

    public Coordinate closestCoordinateFrom(Coordinate target) {
        Coordinate closestCoordinate = startCoordinate();
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
        coordinates.add(endCoordinate());
        return coordinates;
    }

    public Coordinate startCoordinate() {
        return lines.getFirst().start();
    }

    private Coordinate endCoordinate() {
        return lines.getLast().end();
    }

    private Segment merge(Segment other) {
        List<GeoLine> mergedCoordinates = new ArrayList<>();
        mergedCoordinates.addAll(this.lines);
        mergedCoordinates.addAll(other.lines);
        return new Segment(mergedCoordinates);
    }

    // 경향성이 같은 것끼리 합친다.
    private static List<Segment> mergeSameDirection(List<Segment> segments) {
        List<Segment> mergedSegments = new ArrayList<>();
        mergedSegments.add(segments.getFirst());
        for (int i = 1; i < segments.size(); i++) {
            Segment beforeSegment = mergedSegments.removeLast();
            Segment currentSegment = segments.get(i);

            if (beforeSegment.isSameDirection(currentSegment)) {
                mergedSegments.add(beforeSegment.merge(currentSegment));
            } else {
                mergedSegments.add(beforeSegment);
                mergedSegments.add(currentSegment);
            }
        }
        return mergedSegments;
    }

    private boolean isSameDirection(Segment other) {
        double startElevation = startCoordinate().elevation();
        double endElevation = endCoordinate().elevation();
        double otherStartElevation = other.startCoordinate().elevation();
        double otherEndElevation = other.endCoordinate().elevation();

        double elevationDiff = startElevation - endElevation;
        double otherElevationDiff = otherStartElevation - otherEndElevation;

        return Math.signum(elevationDiff) == Math.signum(otherElevationDiff);
    }

    // 경사타입이 같은 것끼리 합친다.
    private static List<Segment> mergeSameInclineType(List<Segment> segments) {
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
}
