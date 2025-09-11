package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.Difficulty;
import coursepick.coursepick.domain.InclineSummary;
import coursepick.coursepick.domain.RoadType;

import java.util.List;

public record CourseMetaData(
        List<Difficulty> difficulties,
        List<InclineSummary> inclineSummaries,
        List<RoadType> roadTypes
) {
}
