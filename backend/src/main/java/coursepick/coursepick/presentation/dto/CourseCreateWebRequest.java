package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Difficulty;
import coursepick.coursepick.domain.course.RoadType;

import java.util.List;

public record CourseCreateWebRequest(
        String name,
        RoadType roadType,
        Difficulty difficulty,
        List<Coordinate> coordinates
) {
}
