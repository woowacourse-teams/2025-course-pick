package coursepick.coursepick.domain.course;

import java.util.ArrayList;
import java.util.List;

public record Segment(
        List<GeoLine> lines
) {
    public InclineType inclineType() {
        return InclineType.of(startCoordinate(), endCoordinate());
    }

    public Coordinate startCoordinate() {
        return lines.getFirst().start();
    }

    private Coordinate endCoordinate() {
        return lines.getLast().end();
    }

    public Meter length() {
        return lines.stream()
                .map(GeoLine::length)
                .reduce(Meter.zero(), Meter::add);
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

    public boolean isSameDirection(Segment other) {
        double startElevation = startCoordinate().elevation();
        double endElevation = endCoordinate().elevation();
        double otherStartElevation = other.startCoordinate().elevation();
        double otherEndElevation = other.endCoordinate().elevation();

        double elevationDiff = startElevation - endElevation;
        double otherElevationDiff = otherStartElevation - otherEndElevation;

        return Math.signum(elevationDiff) == Math.signum(otherElevationDiff);
    }

    public Segment merge(Segment other) {
        List<GeoLine> mergedCoordinates = new ArrayList<>();
        mergedCoordinates.addAll(this.lines);
        mergedCoordinates.addAll(other.lines);
        return new Segment(mergedCoordinates);
    }
}
