package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.RoadType;

import java.util.List;

public record CourseReplaceWebRequest(
        List<List<Double>> coordinates,
        String name,
        RoadType roadType
) {
}
