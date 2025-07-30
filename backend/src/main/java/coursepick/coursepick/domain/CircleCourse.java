package coursepick.coursepick.domain;

import coursepick.coursepick.application.exception.ErrorType;
import jakarta.persistence.DiscriminatorValue;

import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("Circle")
@NoArgsConstructor
public class CircleCourse extends Course {

    public CircleCourse(String name, RoadType roadType, List<Coordinate> coordinates) {
        super(name, roadType, sortByCounterClockwise(coordinates));
        validateIsCircle(this.coordinates);
    }

    public CircleCourse(String name, List<Coordinate> coordinates) {
        this(name, RoadType.알수없음, coordinates);
    }

    @Override
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

    private void validateIsCircle(List<Coordinate> coordinates) {
        if (!coordinates.getFirst().hasSameLatitudeAndLongitude(coordinates.getLast())) {
            throw new IllegalArgumentException(ErrorType.NOT_CONNECTED_CIRCLE_COURSE.message());
        }
    }

    private static List<Coordinate> sortByCounterClockwise(List<Coordinate> coordinates) {
        int lowestCoordinateIndex = findLowestCoordinateIndex(coordinates);
        List<Coordinate> counterClockWiseCoordinates = new ArrayList<>(coordinates);
        if (isClockwise(coordinates, lowestCoordinateIndex)) {
            Collections.reverse(counterClockWiseCoordinates);
        }
        return counterClockWiseCoordinates;
    }

    private static int findLowestCoordinateIndex(List<Coordinate> coordinates) {
        int lowestCoordinateIndex = 0;
        double lowestLatitude = Double.MAX_VALUE;
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate coordinate = coordinates.get(i);
            if (coordinate.latitude() < lowestLatitude) {
                lowestLatitude = coordinate.latitude();
                lowestCoordinateIndex = i;
            }
        }
        return lowestCoordinateIndex;
    }

    private static boolean isClockwise(List<Coordinate> coordinates, int lowestCoordinateIndex) {
        int nextIndex = (lowestCoordinateIndex + 1) % coordinates.size();
        return coordinates.get(lowestCoordinateIndex).isRightOf(coordinates.get(nextIndex));
    }
}
