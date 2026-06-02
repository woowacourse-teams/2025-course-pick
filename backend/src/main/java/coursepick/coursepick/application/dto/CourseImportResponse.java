package coursepick.coursepick.application.dto;

import java.util.List;

public record CourseImportResponse(
        int successCount,
        List<String> successNames,
        int skippedCount,
        List<String> skippedReasons
) {
}
