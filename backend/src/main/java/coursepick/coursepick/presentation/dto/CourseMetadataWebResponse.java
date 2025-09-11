package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CourseMetaData;
import coursepick.coursepick.domain.Difficulty;
import coursepick.coursepick.domain.InclineSummary;
import coursepick.coursepick.domain.RoadType;

import java.util.List;

public record CourseMetadataWebResponse(
        List<Difficulty> difficulties,
        List<InclineSummary> inclineSummaries,
        List<RoadType> roadTypes
) {
    public static CourseMetadataWebResponse from(CourseMetaData courseMetaData) {
        return new CourseMetadataWebResponse(
                courseMetaData.difficulties(),
                courseMetaData.inclineSummaries(),
                courseMetaData.roadTypes()
        );
    }
}
