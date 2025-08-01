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
        @Column(name = "geolines", columnDefinition = "LONGTEXT")
        List<GeoLine> lines
) {
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

    public boolean isSameDirection(Segment other) {
        double startElevation = startCoordinate().elevation();
        double endElevation = endCoordinate().elevation();
        double otherStartElevation = other.startCoordinate().elevation();
        double otherEndElevation = other.endCoordinate().elevation();

        double elevationDiff = startElevation - endElevation;
        double otherElevationDiff = otherStartElevation - otherEndElevation;

        return Math.signum(elevationDiff) == Math.signum(otherElevationDiff);
    }

    public Coordinate startCoordinate() {
        return lines.getFirst().start();
    }

    public Segment merge(Segment other) {
        List<GeoLine> mergedCoordinates = new ArrayList<>();
        mergedCoordinates.addAll(this.lines);
        mergedCoordinates.addAll(other.lines);
        return new Segment(mergedCoordinates);
    }

    private Coordinate endCoordinate() {
        return lines.getLast().end();
    }
}
