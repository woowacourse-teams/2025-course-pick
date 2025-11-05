package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.RoadType;

import java.util.List;

public record CourseRelaceWebRequest(
        List<List<Double>> coordinates,
        String name,
        RoadType roadType
) {
}
