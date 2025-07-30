package coursepick.coursepick.domain;

import jakarta.persistence.DiscriminatorValue;

import jakarta.persistence.Entity;

import java.util.List;

import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("Line")
@NoArgsConstructor
public class LineCourse extends Course {

    public LineCourse(String name, RoadType roadType, List<Coordinate> coordinates) {
        super(name, roadType, coordinates);
    }

    public LineCourse(String name, List<Coordinate> coordinates) {
        this(name, RoadType.알수없음, coordinates);
    }

    @Override
    public Coordinate closestCoordinateFrom(Coordinate target) {
        Coordinate first = this.coordinates.getFirst();
        Coordinate last = this.coordinates.getLast();

        if (first.equals(target)) {
            return first;
        }

        return last;
    }
}
