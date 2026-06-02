package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CourseImportResponse;
import java.util.List;

public record CourseImportWebResponse(
        int successCount,
        List<String> successNames,
        int skippedCount,
        List<String> skippedReasons
) {
    public static CourseImportWebResponse from(CourseImportResponse response) {
        return new CourseImportWebResponse(
                response.successCount(),
                response.successNames(),
                response.skippedCount(),
                response.skippedReasons()
        );
    }
}
